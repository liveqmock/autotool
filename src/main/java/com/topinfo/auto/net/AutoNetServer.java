package com.topinfo.auto.net;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.topinfo.util.ConfigHelper;

/**
  * @ClassName: AutoNetServer
  * @Description: TODO
  * @author tyler.wu-whz
  * @date 2013-3-8 下午1:38:37
  *
  */
public class AutoNetServer {

    static ConfigHelper config = new ConfigHelper ();

    public static void run(){

        Selector selector = null;
        ServerSocketChannel serverSocketChannel = null;

        try {
            // Selector for incoming time requests
            selector = Selector.open ();

            // Create a new server socket and set to non blocking mode
            serverSocketChannel = ServerSocketChannel.open ();
            serverSocketChannel.configureBlocking (false);

            // Bind the server socket to the local host and port
            serverSocketChannel.socket ().setReuseAddress (true);
            serverSocketChannel.socket ().bind (new InetSocketAddress (10000));

            // Register accepts on the server socket with the selector. This
            // step tells the selector that the socket wants to be put on the
            // ready list when accept operations occur, so allowing multiplexed
            // non-blocking I/O to take place.
            serverSocketChannel.register (selector, SelectionKey.OP_ACCEPT);

            System.out.println ("stratting server ....................");

            // Here's where everything happens. The select method will
            // return when any operations registered above have occurred, the
            // thread has been interrupted, etc.
            while (selector.select () > 0) {
                // Someone is ready for I/O, get the ready keys
                Iterator<SelectionKey> it = selector.selectedKeys ().iterator ();

                System.out.println ("have read  key ....");
                // Walk through the ready keys collection and process date
                // requests.
                while (it.hasNext ()) {
                    SelectionKey readyKey = it.next ();
                    it.remove ();
                    System.out.println ("have read  key event ....");
                    // The key indexes into the selector so you
                    // can retrieve the socket that's ready for I/O
                    doit ((ServerSocketChannel) readyKey.channel ());
                }
            }
        } catch (ClosedChannelException ex) {
            System.out.println (ex);
        } catch (IOException ex) {
            System.out.println (ex);
        } finally {
            try {
                selector.close ();
            } catch (Exception ex) {
                System.out.println (ex);
            }
            try {
                serverSocketChannel.close ();
            } catch (Exception ex) {
                System.out.println (ex);
            }
        }
    }

    private static void doit(final ServerSocketChannel serverSocketChannel) throws IOException{
        SocketChannel socketChannel = null;
        try {
            socketChannel = serverSocketChannel.accept ();
            // 做一个接文件的操作
            receiveFile (socketChannel, new File ("/home/app.zip"));
            //
            System.out.println ("start 接收文件 ");
            Runtime rt = Runtime.getRuntime ();
            String command = "sh " + AutoNetServer.config.startServerdir + "/updateApp.sh";
            Process pcs = rt.exec (command);
            PrintWriter outWriter = new PrintWriter (new File (AutoNetServer.config.shlogdir));
            BufferedReader br = new BufferedReader (new InputStreamReader (pcs.getInputStream ()));
            String line = new String ();
            while ((line = br.readLine ()) != null) {
                System.out.println (line);
                outWriter.write (line);
            }
            try {
                pcs.waitFor ();
            } catch (InterruptedException e) {
                System.err.println ("processes was interrupted");
            }
            br.close ();
            outWriter.flush ();
            outWriter.close ();
            int ret = pcs.exitValue ();
            System.out.println ("执行完毕！！");
        } catch (Exception e) {
            System.out.println (e);
        } finally {
            try {
                System.out.println ("send  end");
            } catch (Exception ex) {
                System.out.println (ex);
            }
        }

    }

    private static void receiveFile(SocketChannel socketChannel,File file) throws IOException{
        FileOutputStream fos = null;
        FileChannel channel = null;

        try {
            // 用于把数据从管道上拿下来变成文件
            fos = new FileOutputStream (file);
            channel = fos.getChannel ();
            socketChannel.read (ByteBuffer.allocateDirect (1));
            ByteBuffer buffer = ByteBuffer.allocateDirect (2048);
            int size = 0;
            while ((size = socketChannel.read (buffer)) != -1) {
                buffer.flip ();
                if (size > 0) {
                    buffer.limit (size);
                    channel.write (buffer);
                    buffer.clear ();
                }
            }
        } catch (Exception e) {
            System.out.println (e);
        } finally {
            try {
                fos.close ();
            } catch (Exception ex) {
                System.out.println (ex);
            }
        }
    }
}