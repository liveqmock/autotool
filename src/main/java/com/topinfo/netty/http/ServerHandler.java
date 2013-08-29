package com.topinfo.netty.http;

import static org.jboss.netty.handler.codec.http.HttpHeaders.*;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.*;
import static org.jboss.netty.handler.codec.http.HttpMethod.*;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.*;
import static org.jboss.netty.handler.codec.http.HttpVersion.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelFutureProgressListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.DefaultFileRegion;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.FileRegion;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.frame.TooLongFrameException;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.ssl.SslHandler;
import org.jboss.netty.handler.stream.ChunkedFile;
import org.jboss.netty.util.CharsetUtil;

import com.topinfo.AutoServer;
import com.topinfo.model.AutoConf;
import com.topinfo.model.AutoUpdate;

/**
  * @ClassName: ServerHandler
  * @Description: TODO
  * @author Comsys-whz
  * @date 2013-3-14 下午4:33:12
  *
  */
public class ServerHandler extends SimpleChannelUpstreamHandler {

    Logger logger = Logger.getLogger (ServerHandler.class);

    /*
     * <p>Title: channelConnected</p>
     * <p>Description: </p>
     * @param ctx
     * @param e
     * @throws Exception
     * @see org.jboss.netty.channel.SimpleChannelUpstreamHandler#channelConnected(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.ChannelStateEvent)
     */

    @Override
    public void channelConnected(ChannelHandlerContext ctx,ChannelStateEvent e) throws Exception{
        // TODO Auto-generated method stub
        super.channelConnected (ctx, e);
        logger.error ("客户端连接上了");
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx,MessageEvent e) throws Exception{
        HttpRequest request = (HttpRequest) e.getMessage ();
        String cmd = request.getHeader ("cmd");
        logger.error ("接收到请求 " + request);
        if (cmd.equals ("file")) {
            downFile (ctx, e);
        } else {
            writeClientLog (request, e);
        }
        // 非法请求返回
        if (request.getMethod () != GET) {
            sendError (ctx, METHOD_NOT_ALLOWED);
            return;
        }

    }

    private void downFile(ChannelHandlerContext ctx,MessageEvent e) throws Exception{

        HttpRequest request = (HttpRequest) e.getMessage ();

        String appid = request.getHeader ("appid");
        String version = request.getHeader ("version");
        // 查询最新版本
        AutoConf app = AutoServer.commondao.getAppVersionByAppid (new Integer (appid));
        String path = null;
        if (app != null && app.getVersion () > new Integer (version)) path = app.getAppPath ();
        logger.debug (path);
        if (path == null) {
            sendError (ctx, FORBIDDEN);
            return;
        }
        File file = new File (path);
        if (file.isHidden () || !file.exists ()) {
            sendError (ctx, NOT_FOUND);
            return;
        }
        if (!file.isFile ()) {
            sendError (ctx, FORBIDDEN);
            return;
        }
        RandomAccessFile raf;
        try {
            raf = new RandomAccessFile (file,"r");
        } catch (FileNotFoundException fnfe) {
            sendError (ctx, NOT_FOUND);
            return;
        }
        long fileLength = raf.length ();
        HttpResponse response = new DefaultHttpResponse (HTTP_1_1,OK);
        response.setHeader ("appid", appid);
        response.setHeader ("cmd", "file");
        response.setHeader ("fileName", path);
        response.setHeader ("version", app.getVersion ());
        setContentLength (response, fileLength);
        Channel ch = e.getChannel ();
        ch.write (response);
        ChannelFuture writeFuture;
        final String temp = path;
        if (ch.getPipeline ().get (SslHandler.class) != null) {
            writeFuture = ch.write (new ChunkedFile (raf,0,fileLength,8192));
        } else {
            final FileRegion region = new DefaultFileRegion (raf.getChannel (),0,fileLength);
            writeFuture = ch.write (region);
            writeFuture.addListener (new ChannelFutureProgressListener () {

                public void operationComplete(ChannelFuture future){
                    region.releaseExternalResources ();
                }

                public void operationProgressed(ChannelFuture future,long amount,long current,long total){
                    System.out.printf ("%s: %d / %d (+%d)%n", temp, current, total, amount);
                    // 写日志
                }
            });
        }
        logger.debug (response.isChunked ());

        if (!isKeepAlive (request)) {

            writeFuture.addListener (ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,ExceptionEvent e) throws Exception{
        Channel ch = e.getChannel ();
        Throwable cause = e.getCause ();
        logger.error ("请求处理错误 " + e.getCause ());
        if (cause instanceof TooLongFrameException) {
            sendError (ctx, BAD_REQUEST);
            return;
        }
        cause.printStackTrace ();
        if (ch.isConnected ()) {
            sendError (ctx, INTERNAL_SERVER_ERROR);
        }
    }

    private String sanitizeUri(String uri){
        try {
            uri = URLDecoder.decode (uri, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            try {
                uri = URLDecoder.decode (uri, "ISO-8859-1");
            } catch (UnsupportedEncodingException e1) {
                throw new Error ();
            }
        }
        uri = uri.replace ('/', File.separatorChar);
        if (uri.contains (File.separator + ".") || uri.contains ("." + File.separator) || uri.startsWith (".") || uri.endsWith (".")) { return null; }
        return uri;
    }

    private void sendError(ChannelHandlerContext ctx,HttpResponseStatus status){
        HttpResponse response = new DefaultHttpResponse (HTTP_1_1,status);
        response.setHeader (CONTENT_TYPE, "text/plain; charset=UTF-8");
        response.setContent (ChannelBuffers.copiedBuffer ("Failure: " + status.toString () + "\r\n", CharsetUtil.UTF_8));
        ctx.getChannel ().write (response).addListener (ChannelFutureListener.CLOSE);
    }

    // 接收客户端日志
    public void writeClientLog(HttpRequest request,MessageEvent e){
        AutoUpdate update = new AutoUpdate ();
        update.setAppid (Integer.parseInt (request.getHeader ("appid")));
        update.setIp (request.getHeader ("ip"));
        update.setJgdm (request.getHeader ("jgdm"));
        update.setVersion (Integer.parseInt (request.getHeader ("version")));
        update.setIp (request.getHeader ("ip"));
        update.setStatus (Integer.parseInt (request.getHeader ("status")));
        HttpResponse response = new DefaultHttpResponse (HTTP_1_1,OK);
        response.setHeader ("cmd", "log");
        AutoServer.commondao.insertAutoUpdateLog (update);
        e.getChannel ().write (response);
    }

}