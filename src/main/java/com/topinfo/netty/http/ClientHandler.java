package com.topinfo.netty.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.jboss.netty.handler.codec.http.HttpResponseStatus.*;
import static org.jboss.netty.handler.codec.http.HttpVersion.*;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.HttpResponse;

import com.topinfo.AutoServer;
import com.topinfo.model.AutoConf;
import com.topinfo.model.AutoUpdate;
import com.topinfo.util.ConfigHelper;

/**
  * @ClassName: ClientHandler
  * @Description: TODO
  * @author Comsys-whz
  * @date 2013-3-14 下午4:32:39
  *
  */
public class ClientHandler extends SimpleChannelUpstreamHandler {

    private volatile boolean readingChunks;
    private File             downloadFile;
    private FileOutputStream fOutputStream = null;

    private static int       version       = 0;
    private static int       appid         = 0;
    Logger                   logger        = Logger.getLogger (ClientHandler.class);
    public ExecutorService  ec = Executors.newFixedThreadPool(1);

    @Override
    public void messageReceived(ChannelHandlerContext ctx,MessageEvent e) throws Exception{
        if (e.getMessage () instanceof HttpResponse) {
            DefaultHttpResponse httpResponse = (DefaultHttpResponse) e.getMessage ();
            String action = httpResponse.getHeader ("cmd");
            if (action.equals ("file")) {
                String fileName = httpResponse.getHeader ("fileName");
                logger.debug (fileName);
                downloadFile = new File (fileName);
                fOutputStream = new FileOutputStream (downloadFile);
                ChannelBuffer buffer = httpResponse.getContent ();
                byte[] dst = new byte[buffer.readableBytes ()];
                buffer.readBytes (dst);
                fOutputStream.write (dst);
                readingChunks = httpResponse.isChunked ();
                appid = Integer.parseInt (httpResponse.getHeader ("appid"));
                version = Integer.parseInt (httpResponse.getHeader ("version"));
            } else {
                // 处理其他的业务请求的结果集
                DefaultHttpResponse httpResponse1 = (DefaultHttpResponse) e.getMessage ();

                httpResponse1.getStatus ();
                if (httpResponse1.getStatus () == OK) logger.debug ("log 上传成功");
                logger.debug (httpResponse1.getStatus ());
                e.getChannel ().close ();

            }

        } else {
            HttpChunk httpChunk = (HttpChunk) e.getMessage ();
            if (!httpChunk.isLast ()) {
                ChannelBuffer buffer = httpChunk.getContent ();
                if (fOutputStream == null) {
                    fOutputStream = new FileOutputStream (downloadFile);
                }
                while (buffer.readable ()) {
                    byte[] dst = new byte[buffer.readableBytes ()];
                    buffer.readBytes (dst);
                    fOutputStream.write (dst);
                }
            } else {
                readingChunks = false;
            }
            fOutputStream.flush ();
        }
        if (!readingChunks) {
            fOutputStream.close ();
            e.getChannel ().close ();
            // 执行shell
            try {
                logger.error ("文件处理完，开始执行脚本...");
                AutoConf c = AutoServer.commondao.getAppVersionByAppid (appid);
                String param = c.getAppPath ();
                exec (param);
            } catch (Exception e2) {
                AutoUpdate update = new AutoUpdate ();
                update.setAppid (appid);
                update.setIp (ConfigHelper.localjgdm);
                update.setJgdm (ConfigHelper.localjgdm);
                update.setVersion (version);
                update.setStatus (-1);
                HttpResponse response = new DefaultHttpResponse (HTTP_1_1,OK);
                response.addHeader ("cmd", "log");
                AutoServer.commondao.insertAutoUpdateLog (update);
                logger.error ("本地执行APP脚本出错: appid=" + appid + ";version=" + version, e2);
            }

        }
    }
    
    private void exec(String param) throws Exception{
        Runtime rt = Runtime.getRuntime ();
        Map<String, String> par = dealParam (param);
        String shdir = par.get("shelldir");
        String cmd = par.get("cmd");
        String command = "sh " + shdir + "updateApp.sh " + cmd;
        logger.error ( "pcs.command()" + command);
        Process pcs = rt.exec (command);
        PrintWriter outWriter = new PrintWriter (new File (ConfigHelper.shlogdir));
        BufferedReader br = new BufferedReader (new InputStreamReader (pcs.getInputStream ()));
        BufferedReader ebr = new BufferedReader (new InputStreamReader (pcs.getErrorStream()));
        String line = new String ();
        while ((line = br.readLine ()) != null) {
            outWriter.write (line);
        }
        while ((line = ebr.readLine ()) != null) {
            outWriter.write (line);
        }
        pcs.getInputStream().close(); 
        pcs.getOutputStream().close(); 
        pcs.getErrorStream().close(); 
        AutoUpdate update = new AutoUpdate ();
        try {
            if(br!=null)
                br.close ();
            if(ebr!=null)
            	ebr.close();
        	logger.error ( "pcs.waitFor()"+"ooooookkkkk"+pcs.waitFor());
            AutoConf conf = new AutoConf ();
            conf.setAppid (appid);
            conf.setVersion (version);
            AutoServer.commondao.updateAppVersion (conf);
            update.setStatus (1);
        } catch (Exception e) {
            logger.error ("processes was interrupted",e);
            update.setStatus (-1);
        } finally {
	        update.setAppid (appid);
	        update.setIp (ConfigHelper.localjgdm);
	        update.setJgdm (ConfigHelper.localjgdm);
	        update.setVersion (version);
	        AutoServer.commondao.insertAutoUpdateLog (update);
	        logger.error ("脚本执行完毕...");
        }


    }

    public static Map<String, String> dealParam(String param){
    	Map<String, String> map = new HashMap<String, String>();
        String cmd = " ";
        String fileName = param.substring (param.lastIndexOf ("/") + 1);
        String dir = param.substring (0, param.lastIndexOf ("/"));
        String appName = fileName.substring (0, fileName.lastIndexOf ("."));
        cmd = cmd + dir + " " + appName + " " + appName;
        String shelldir = dir+File.separator+appName+File.separator+"conf"+File.separator;
        map.put("cmd", cmd);
        map.put("shelldir", shelldir);
        return map;
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx,org.jboss.netty.channel.ChannelStateEvent e) throws Exception{
        logger.info ("连接上" + e);
    };

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,ExceptionEvent e) throws Exception{
        logger.error (e);
    }
    
    
    class StreamGobbler extends Thread  
    {  
        InputStream is;  
        String type;  
          
        StreamGobbler(InputStream is, String type)  
        {  
            this.is = is;  
            this.type = type;  
        }  
          
        public void run()  
        {  
            try  
            {  
                InputStreamReader isr = new InputStreamReader(is);  
                BufferedReader br = new BufferedReader(isr);  
                String line=null;  
                while ( (line = br.readLine()) != null)  
                    System.out.println(type + ">" + line);      
                } catch (IOException ioe)  
                  {  
                    ioe.printStackTrace();    
                  }  
        }  
    }

    public static void main(String[] args){
        System.out.println (dealParam ("/home/app/dataE-node.zip"));
    }
}