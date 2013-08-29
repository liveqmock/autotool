/**
 * @Title: AutoServer.java
 * @Package com.topinfo
 * @Description: TODO
 *               Company:图讯科技
 * @author Comsys-whz
 * @date 2013-3-12 下午2:10:42
 * @version V1.0
 */

package com.topinfo;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.mysql.jdbc.StringUtils;
import com.topinfo.db.dao.DefaultCommonDao;
import com.topinfo.model.AutoConf;
import com.topinfo.model.AutoUpdate;
import com.topinfo.model.ServerIp;
import com.topinfo.netty.http.Server;
import com.topinfo.netty.http.ServerClient;
import com.topinfo.util.ConfigHelper;

import freemarker.template.utility.StringUtil;

/**
 * @ClassName: AutoServer
 * @Description: TODO
 * @author tyler.wu-whz
 * @date 2013-3-12 下午2:10:42
 * 
 */

public class AutoServer {

    static Logger     logger = Logger.getLogger (AutoServer.class);
    public static DefaultCommonDao commondao;
    public static ConfigHelper     config = new ConfigHelper ();

    public static void main(String[] args){
        // 启动spring
        ApplicationContext context = new ClassPathXmlApplicationContext ("applicationContext.xml");
        // 查找或配置 URI
        commondao = (DefaultCommonDao) context.getBean ("commonDao"); 
        // 启动客户端程序 ---- 去请求是否有更新程序 1分钟后执行
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor ();
        final HttpRequest request = new DefaultHttpRequest (HttpVersion.HTTP_1_1,HttpMethod.GET,"httpGet");
        // 定期检查 APP是否更新
        scheduledExecutorService.scheduleAtFixedRate (new Runnable () {

            @Override
            public void run(){
                final List<AutoConf> list = commondao.getNeedApp ();
                ServerIp serverIp = AutoServer.getSuperiorIp ();
                if (StringUtils.isNullOrEmpty(serverIp.getIp())) return;
                String uri = serverIp.getIp ();
                for ( AutoConf autoConf : list ) {
                    request.clearHeaders ();
                    request.setHeader ("cmd", "file");
                    request.setHeader ("appid", autoConf.getAppid ());
                    request.setHeader ("appName", autoConf.getAppName ());
                    request.setHeader ("version", autoConf.getVersion ());
                    request.setHeader ("appDb", autoConf.getAppDb ());
                    request.setHeader ("jgdm", ConfigHelper.localjgdm);
                    logger.debug ("发起请求" + request);
                    ServerClient.connectRequest (uri, request);
                }
            }
        }, 1, ConfigHelper.rate, TimeUnit.MINUTES);
        // 上传最新程序日志版本 查询各个最新版本 1分钟后执行
        scheduledExecutorService.scheduleAtFixedRate (new Runnable () {

            @Override
            public void run(){
                final List<AutoUpdate> logs = commondao.getAllLog ();
                ServerIp serverIp = AutoServer.getSuperiorIp ();
                if (StringUtils.isNullOrEmpty(serverIp.getIp())) return;
                String uri = serverIp.getIp ();
                for ( AutoUpdate update : logs ) {
                    request.clearHeaders ();
                    request.setHeader ("cmd", "log");
                    request.setHeader ("appid", update.getAppid ());
                    request.setHeader ("version", update.getVersion ());
                    request.setHeader ("jgdm", update.getJgdm ());
                    request.setHeader ("ip", update.getIp ());
                    request.setHeader ("status", update.getStatus ());
                    ServerClient.connectRequest (uri, request);
                    logger.debug ("发起请求" + request);
                }
            }
        }, 1, ConfigHelper.rate + 10, TimeUnit.MINUTES);

        // 启动服务端
        Server.run (ConfigHelper.port);
        logger.error ("AutoServer服务启动...");
    }

    public static ServerIp getSuperiorIp(){
//        ServerIp myip = commondao.getServerMYiP (ConfigHelper.localjgdm);
//        ServerIp superior = null;
//        if (myip != null) superior = commondao.getServerSuperioriP (myip.getLocalGrade ());
//        return superior;
    	ServerIp ip = new ServerIp();
    	ip.setIp(ConfigHelper.superiorIp);
    	return ip;
    }
}
