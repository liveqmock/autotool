package com.topinfo.netty.http;




import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.handler.stream.ChunkedWriteHandler;

/**
  * @ClassName: Server
  * @Description: TODO
  * @author Comsys-whz
  * @date 2013-3-14 下午4:32:31
  *
  */
public class Server  
{  
    public static void run(int port)  
    {  
  
        ServerBootstrap bootstrap = new ServerBootstrap(  
                new NioServerSocketChannelFactory(  
                        Executors.newCachedThreadPool(),  
                        Executors.newCachedThreadPool()));  
  
        bootstrap.setPipelineFactory(new ChannelPipelineFactory()  
        {  
            public ChannelPipeline getPipeline() throws Exception  
            {  
            	ChannelPipeline pipeline = Channels.pipeline();
                pipeline.addLast("decoder", new HttpRequestDecoder());  
                pipeline.addLast("aggregator", new HttpChunkAggregator(65536));  
                pipeline.addLast("encoder", new HttpResponseEncoder());  
                pipeline.addLast("chunkedWriter", new ChunkedWriteHandler());  
                pipeline.addLast("handler", new ServerHandler());  
                return pipeline;  
            }  
  
        });  
  
        bootstrap.bind(new InetSocketAddress(port));  
        System.out.println("服务器起来啦");
    }  
    
    
    public static void main(String[] args) {
    	Server.run(10000);
	}
} 