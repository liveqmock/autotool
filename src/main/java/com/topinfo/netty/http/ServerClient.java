package com.topinfo.netty.http;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpRequestEncoder;
import org.jboss.netty.handler.codec.http.HttpResponseDecoder;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.handler.stream.ChunkedWriteHandler;

import com.topinfo.util.ConfigHelper;

/**
 * @ClassName: ServerClient
 * @Description: TODO
 * @author Comsys-whz
 * @date 2013-3-14 下午4:32:56
 * 
 */
public class ServerClient {

    static Logger      logger    = Logger.getLogger (ServerClient.class);
    public static ChannelFuture   future    = null;
    public static ClientBootstrap bootstrap = null;

    public static void connectRequest(String uri,HttpRequest request){
        bootstrap = new ClientBootstrap (new NioClientSocketChannelFactory (Executors.newCachedThreadPool (),Executors.newCachedThreadPool ()));
        bootstrap.setPipelineFactory (new ChannelPipelineFactory () {

            @Override
            public ChannelPipeline getPipeline() throws Exception{
                ChannelPipeline pipeline = Channels.pipeline ();
                pipeline.addLast ("decoder", new HttpResponseDecoder ());
                pipeline.addLast ("encoder", new HttpRequestEncoder ());
                pipeline.addLast ("chunkedWriter", new ChunkedWriteHandler ());
                pipeline.addLast ("handler", new ClientHandler ());
                return pipeline;
            }
        });
        future = bootstrap.connect (new InetSocketAddress (uri,ConfigHelper.port));
        try {
            Thread.sleep (1000);
        } catch (InterruptedException e) {
            logger.error(e);
        }
        future.getChannel ().write (request);
        future.getChannel ().getCloseFuture ().awaitUninterruptibly ();
        bootstrap.releaseExternalResources ();
    }

    public static void main(String[] args){
        final HttpRequest request = new DefaultHttpRequest (HttpVersion.HTTP_1_1,HttpMethod.GET,"");
        ServerClient.connectRequest ("127.0.0.1", request);
    }
}
