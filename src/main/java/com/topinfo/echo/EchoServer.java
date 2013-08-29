/**
 * @Title: EchoServer.java
 * @Package echo
 * @Description: TODO
 *               Copyright: Copyright (c) 2011
 *               Company:浙江图讯科技有限公司
 * @author Comsys-whz
 * @date 2013-3-11 下午1:33:10
 * @version V1.0
 */

package com.topinfo.echo;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * @ClassName: EchoServer
 * @Description: TODO
 * @author Comsys-whz
 * @date 2013-3-11 下午1:33:10
 * 
 */

public class EchoServer {

    private Selector            selector            = null;
    private ServerSocketChannel serverSocketChannel = null;
    private int                 port                = 8000;
    private Charset             charset             = Charset.forName ("GBK");
    private String              FILE_PATH           = "D:\\down";

    public EchoServer() throws IOException {
        selector = Selector.open ();
        serverSocketChannel = ServerSocketChannel.open ();
        serverSocketChannel.configureBlocking (false);
        serverSocketChannel.socket ().bind (new InetSocketAddress (port));
        System.out.println ("服务器启动");
    }

    public void service() throws IOException{
        serverSocketChannel.register (selector, SelectionKey.OP_ACCEPT);
        while (selector.select () > 0) {
            Set<SelectionKey> readyKeys = selector.selectedKeys ();
            Iterator<SelectionKey> it = readyKeys.iterator ();
            while (it.hasNext ()) {
                SelectionKey key = null;
                try {
                    key = (SelectionKey) it.next ();
                    it.remove ();
                    if (key.isAcceptable ()) {
                        ServerSocketChannel ssc = (ServerSocketChannel) key.channel ();
                        SocketChannel socketChannel = (SocketChannel) ssc.accept ();
                        System.out.println ("接收到客户连接，来自" + socketChannel.socket ().getInetAddress () + "端口：" + socketChannel.socket ().getPort ());
                        socketChannel.configureBlocking (false);
                        ByteBuffer buffer = ByteBuffer.allocate (1024);
                        socketChannel.register (selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE, buffer);
                    }
                    if (key.isReadable ()) {
                        receive (key);
                    }
                } catch (IOException e) {
                    e.printStackTrace ();
                }
            }
        }
    }

    public void receive(SelectionKey key) throws IOException{
        SocketChannel socketChannel = (SocketChannel) key.channel ();
        ByteBuffer readBuf = ByteBuffer.allocate (3);
        socketChannel.read (readBuf);// 把通道的数据读到readBuf中
        readBuf.flip ();
        String s = decode (readBuf);
        if (s.equals ("GET"))// 如果是GET 则准备发送文件
        {
            try {
                int bytePath = getFileLength (socketChannel);
                String path = getPath (socketChannel, bytePath);// 获得文件路径
                FileInputStream fis = new FileInputStream (path);// 文件输入流

                String erro = "yes";
                ByteBuffer buffer = ByteBuffer.allocate (3);
                buffer.put (encode (erro));
                buffer.flip ();
                while (buffer.hasRemaining ())
                    socketChannel.write (buffer);

                int bytes = fis.available ();// 获得文件总字节数
                socketChannel.write (encode (String.valueOf (bytes) + "/r/n"));// 发送文件大小
                System.out.println ("文件大小：" + bytes + "字节");
                int filebytes = path.substring (8).getBytes ("GBK").length;
                socketChannel.write (encode (String.valueOf (filebytes) + "/r/n"));// 发送文件名字的字节数

                socketChannel.write (encode (path.substring (8)));// 发送文件名
                FileChannel fic = fis.getChannel ();// 文件输入流通道
                fic.position (0);
                buffer = ByteBuffer.allocate (1024 * 1024);
                int byteRead = 0;
                double byteWrite = 0;
                System.out.println ("正在发送文件！");
                int b = 0;
                while (byteRead >= 0) {
                    byteRead = fic.read (buffer);
                    if (byteRead == -1) break;
                    buffer.flip ();
                    byteWrite += byteRead;
                    while (b > 0) {
                        System.out.print ('\b');
                        b--;
                    }
                    System.out.print ("已发送完成" + (int) (byteWrite * 100 / bytes) + "%");
                    b = ("已发送完成" + String.valueOf ((int) (byteWrite * 100 / bytes)) + "%").getBytes ().length;
                    while (buffer.hasRemaining ())
                        socketChannel.write (buffer);
                    buffer.compact ();
                }
                System.out.println ();
                System.out.println (path + "文件发送完成！");
            } catch (FileNotFoundException e) {
                String erro = "err";
                ByteBuffer buffer = ByteBuffer.allocate (3);
                buffer.put (encode (erro));
                buffer.flip ();
                while (buffer.hasRemaining ())
                    socketChannel.write (buffer);
            }
        } else if (s.equals ("PUT")) {

            int bytePath = getFileLength (socketChannel);
            String path = getPath (socketChannel, bytePath);// 获得文件存放的路径
            int bytes = getFileLength (socketChannel);// 要接受的文件总字节数

            System.out.println ("文件大小：" + bytes + "字节");
            FileOutputStream fos = new FileOutputStream (path);
            FileChannel foc = fos.getChannel ();
            foc.position (0);
            ByteBuffer buffer = ByteBuffer.allocate (1024 * 1024);
            System.out.println ("正在接收文件。。。");
            double byteRead = 0;
            int byteAll = bytes;
            int b = 0;
            while (bytes > 0) {
                if (bytes < 1024 * 1024) {
                    buffer = ByteBuffer.allocate (bytes);
                }
                socketChannel.read (buffer);
                int position = buffer.position ();// 记录当前读到的文件长度
                buffer.flip ();
                while (buffer.hasRemaining ())
                    foc.write (buffer);
                buffer.compact ();
                bytes = bytes - position;
                byteRead += position;
                while (b > 0) {
                    System.out.print ('\b');
                    b--;
                }
                System.out.print ("已完成" + (int) (byteRead * 100 / byteAll) + "%.");
                b = ("已完成" + String.valueOf ((int) (byteRead * 100 / byteAll)) + "%.").getBytes ().length;
            }
            System.out.println (path + "文件接收完成！");
        } else if (s.equals ("bye")) {
            socketChannel.close ();
            key.cancel ();
            System.out.println ("退出程序。");
            System.exit (0);
        }
    }

    // 解码
    public String decode(ByteBuffer buffer){
        CharBuffer charBuffer = charset.decode (buffer);
        return charBuffer.toString ();
    }

    // 编码
    public ByteBuffer encode(String str){
        return charset.encode (str);
    }

    // 获得文件存放路径
    public String getPath(SocketChannel socketChannel,int fileNameLength) throws IOException{
        ByteBuffer readBuf = ByteBuffer.allocate (fileNameLength);
        socketChannel.read (readBuf);
        readBuf.flip ();
        String path = FILE_PATH + "\\" + decode (readBuf);
        return path;
    }

    // 获得文件长度
    public int getFileLength(SocketChannel socketChannel) throws IOException{
        ByteBuffer readBuf = ByteBuffer.allocate (1);
        String acount = " ";
        while (!acount.endsWith ("/r/n")) {
            socketChannel.read (readBuf);
            readBuf.flip ();
            acount += decode (readBuf);
            readBuf.compact ();
        }
        acount = acount.substring (1, acount.indexOf ("/r/n"));
        return Integer.parseInt (acount);
    }

    /**
     * @param args
     */
    public static void main(String[] args){
        // TODO 自动生成方法存根
        try {
            new EchoServer ().service ();
        } catch (IOException e) {
            e.printStackTrace ();
        }
    }

}
