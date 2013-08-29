package com.topinfo.auto.net;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

import com.topinfo.util.ConfigHelper;

/**
 * @ClassName: AutoNetClient
 * @Description: TODO
 * @author tyler.wu-whz
 * @date 2013-3-8 下午1:38:44
 * 
 */
public class AutoNetClient implements Runnable {

    public void run(){
        SocketChannel socketChannel = null;
        try {
            socketChannel = SocketChannel.open ();
            SocketAddress socketAddress = new InetSocketAddress (ConfigHelper.superiorIp,10000);
            socketChannel.socket ().connect (socketAddress);

            // 发送数据
            System.out.println ("client send start..........");
            sendFile (socketChannel, new File ("D:\\adt-bundle-windows-x86_64.zip"));
            System.out.println ("client send end .............");
        } catch (Exception ex) {
            System.out.println (ex);
        } finally {

        }
    }

    private void sendFile(SocketChannel socketChannel,File file) throws IOException{
        FileInputStream fis = null;
        FileChannel channel = null;
        try {
            ByteBuffer buffer = ByteBuffer.allocateDirect (2048);
            // 把文件编程流再方法通道上去
            socketChannel.write (buffer.put ((byte) 0));
            buffer.rewind ();
            fis = new FileInputStream (file);
            channel = fis.getChannel ();

            int size = 0;
            while ((size = channel.read (buffer)) != -1) {
                buffer.rewind ();
                buffer.limit (size);
                socketChannel.write (buffer);
                buffer.clear ();
            }
        } finally {
            try {
                channel.close ();
            } catch (Exception ex) {
                System.out.println (ex);
            }
            try {
                fis.close ();
            } catch (Exception ex) {
                System.out.println (ex);
            }
        }
    }
}
