/**
 * @Title: ConfigHelper.java
 * @Package tool
 * @Description: TODO
 *               Company:图讯科技
 * @author Comsys-whz
 * @date 2013-3-8 下午2:08:04
 * @version V1.0
 */

package com.topinfo.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

/**
 * @ClassName: ConfigHelper
 * @Description: TODO
 * @author tyler.wu-whz
 * @date 2013-3-8 下午2:08:04
 *
 */

public class ConfigHelper {

    static Logger                  logger = Logger.getLogger(ConfigHelper.class);
    /**
    
     * 创建一个新的实例 ConfigHelper. 
     * <p>Title: </p>
     * <p>Description: </p>
     */

    public static String  startServerdir = null;

    public static String  shlogdir       = null;

    public static String  superiorIp     = null;

    public static String  localjgdm      = null;

    public static Integer rate           = 60;
    
    public static Integer port           = null;

    public ConfigHelper() {
        String url = Thread.currentThread ().getContextClassLoader ().getResource ("").getFile () + "autoconf.properties";

        java.util.Properties properties = new java.util.Properties ();
        try {
            InputStream in = new FileInputStream (new File (url));
            properties.load (in);
        } catch (IOException e) {
            logger.error ("autoconf.properties文件加载失败",e);
        } // 得到的是map集合
        startServerdir = properties.getProperty ("startServer.dir");
        shlogdir = properties.getProperty ("server.shlog");
        superiorIp = properties.getProperty ("server.Superior.ip");
        localjgdm = properties.getProperty ("local.jgdm");
        port = Integer.parseInt (properties.getProperty("server.port"));
        try {
            rate = Integer.parseInt (properties.getProperty ("request.rate.MINUTES"));
        } catch (Exception e) {
            rate = 60;
        }

        //System.out.println (superiorIp);
    }

    public static void main(String[] args){
        new ConfigHelper ();
    }
}
