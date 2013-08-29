/**
 * @Title: EchoClient.java
 * @Package echo
 * @Description: TODO
 *               Copyright: Copyright (c) 2011
 *               Company:浙江图讯科技有限公司
 * @author Comsys-whz
 * @date 2013-3-11 下午1:33:20
 * @version V1.0
 */

package com.topinfo.echo;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * @ClassName: EchoClient
 * @Description: TODO
 * @author Comsys-whz
 * @date 2013-3-11 下午1:33:20
 * 
 */

public class EchoClient {

    private SocketChannel socketChannel = null;
    private Selector      selector;
    private String        FILE_PATH     = "E:\\down\\";
    private Charset       charset       = Charset.forName ("GBK");
    private ByteBuffer    sendCMDBuffer = ByteBuffer.allocate (1024); // 存发送命令

    public EchoClient() throws IOException {
        socketChannel = SocketChannel.open ();
        selector = Selector.open ();
        InetAddress is = InetAddress.getLocalHost ();
        InetSocketAddress isa = new InetSocketAddress (is,8000);
        socketChannel.connect (isa);
        socketChannel.configureBlocking (false);
        System.out.println ("与服务器连接成功！");

    }

    public void receiveFromUser()// 接受用户输入
    {
        String i = "PUTC://Users//whz//Desktop//dsss.txt/r/n";
        sendCMDBuffer.put (encode (i));
        // BufferedReader br = new BufferedReader(new InputStreamReader(
        // System.in));
        // String msg = null;
        // while ((msg = br.readLine()) != null) {
        // if (msg.length() < 3) {
        // System.out.println("输入命令有错！！！");
        // }
        // if (msg.equals("bye")) {
        // synchronized (sendCMDBuffer) {
        // sendCMDBuffer.put(encode(msg + "/r/n"));
        // }
        // break;
        // } else if (msg.substring(0, 3).equals("PUT")
        // || msg.substring(0, 3).equals("GET"))
        // synchronized (sendCMDBuffer) {
        // sendCMDBuffer.put(encode(msg + "/r/n"));
        // }
        // else
        // System.out.println("输入命令有错！！！");
        // }

    }

    public void service() throws IOException{
        socketChannel.register (selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);// 注册事件
        while (selector.select () > 0) {
            Set<SelectionKey> readyKeys = selector.selectedKeys ();
            Iterator<SelectionKey> it = readyKeys.iterator ();
            while (it.hasNext ()) {
                SelectionKey key = null;
                try {
                    key = (SelectionKey) it.next ();
                    it.remove ();
                    if (key.isReadable ()) {
                        receive (key);
                    }
                    if (key.isWritable ()) {
                        send (key);
                    }
                    Thread.sleep (100);
                } catch (InterruptedException e) {
                    e.printStackTrace ();
                } catch (FileNotFoundException e) {
                    System.out.println ("文件不存在！");
                    continue;
                } catch (IOException e) {
                    e.printStackTrace ();
                    try {
                        if (key != null) key.cancel ();
                        key.channel ().close ();
                    } catch (Exception ee) {
                        ee.printStackTrace ();
                    }
                }
            }
        }
    }

    public void send(SelectionKey key) throws IOException{
        String subCMD;
        synchronized (sendCMDBuffer) {
            sendCMDBuffer.flip ();
            String CMD = decode (sendCMDBuffer);
            if (CMD.indexOf ("/r/n") == -1)// 还没有完整的命令输入
            {
                sendCMDBuffer.limit (sendCMDBuffer.capacity ());// 把极限设置为容量
                return;
            }
            subCMD = CMD.substring (0, CMD.indexOf ("/r/n"));
            sendCMDBuffer.compact ();
            sendCMDBuffer.put (encode (CMD.substring (CMD.indexOf ("/r/n") + 4)));
        }
        if (subCMD.substring (0, 3).equals ("PUT"))// 如果是SET命令
        {
            String path = subCMD.substring (4);
            int bytePath = path.getBytes ("GBK").length;// 获取文件名字节数
            FileInputStream fis = new FileInputStream (FILE_PATH + path);
            int bytes = fis.available ();// 获得文件总字节数

            socketChannel.write (encode ("PUT" + String.valueOf (bytePath) + "/r/n" + path + String.valueOf (bytes) + "/r/n"));// 发送命令
            System.out.println ("文件大小：" + bytes + "字节");
            FileChannel fic = fis.getChannel ();
            fic.position (0);

            ByteBuffer buffer = ByteBuffer.allocate (1024 * 1024);
            int byteRead = 0;
            System.out.println ("文件正在发送！");
            while (byteRead >= 0) {
                byteRead = fic.read (buffer);
                if (byteRead == -1) break;
                buffer.flip ();

                while (buffer.hasRemaining ())
                    socketChannel.write (buffer);
                buffer.compact ();
            }
            System.out.println (path + "文件发送完成！");
        } else if (subCMD.substring (0, 3).equals ("GET")) {
            String path = subCMD.substring (4);
            int bytePath = path.getBytes ("GBK").length;
            socketChannel.write (encode ("GET" + String.valueOf (bytePath) + "/r/n" + path));// 发送命令
        } else {
            socketChannel.write (encode (subCMD));
            socketChannel.close ();
            key.cancel ();
            System.out.print ("退出程序");
            System.exit (0);
        }
    }

    public void receive(SelectionKey key) throws IOException{
        int bytes;
        ByteBuffer buffer;

        buffer = ByteBuffer.allocate (3);
        socketChannel.read (buffer);
        buffer.flip ();
        String flag = decode (buffer);
        if (flag.equals ("err")) {
            System.out.println ("服务器不存在该文件！");
            return;
        }

        bytes = getFileNameLength (socketChannel);// 获得文件大小
        System.out.println ("文件大小：" + bytes + "字节");

        int byteFileName = getFileNameLength (socketChannel);// 文件名大小
        buffer = ByteBuffer.allocate (byteFileName);
        while (buffer.position () != byteFileName)
            socketChannel.read (buffer);
        buffer.flip ();
        String FileName = decode (buffer);// 获得文件名
        buffer = ByteBuffer.allocate (1024 * 1024);
        FileOutputStream fio = new FileOutputStream (FILE_PATH + FileName);
        FileChannel fic = fio.getChannel ();
        fic.position (0);
        System.out.println ("正在接收文件!");
        while (bytes > 0) {
            if (bytes < 1024 * 1024) {
                buffer = ByteBuffer.allocate (bytes);
            }
            socketChannel.read (buffer);
            int position = buffer.position ();// 记录当前读到的文件长度
            buffer.flip ();
            while (buffer.hasRemaining ())
                fic.write (buffer);
            buffer.compact ();
            bytes = bytes - position;
        }
        System.out.println (FileName + "文件接收完成！");
    }

    public String decode(ByteBuffer buffer){
        CharBuffer charBuffer = charset.decode (buffer);
        return charBuffer.toString ();
    }

    public ByteBuffer encode(String str){
        return charset.encode (str);
    }

    private int getFileNameLength(SocketChannel socketChannel) throws IOException{
        int bytes;
        ByteBuffer buffer = ByteBuffer.allocate (1);
        String acount = " ";
        while (!acount.endsWith ("/r/n"))// 一个字符一个字符读取
        {
            socketChannel.read (buffer);
            buffer.flip ();
            acount += decode (buffer);
            buffer.compact ();
        }
        bytes = Integer.parseInt (acount.substring (1, acount.indexOf ("/r/n")));// 获得文件大小
        return bytes;
    }

    /**
     * @param args
     */
    public static void main(String[] args){
        // TODO 自动生成方法存根
        try {
            final EchoClient ec = new EchoClient ();
            Thread thread = new Thread () {

                public void run(){
                    ec.receiveFromUser ();
                }
            };
            thread.start ();
            ec.service ();
        } catch (IOException e) {
            e.printStackTrace ();
        }
    }

}
