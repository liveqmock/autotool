/**
 * @Title: ServerIp.java
 * @Package com.topinfo.model
 * @Description: TODO
 *               Company:图讯科技
 * @author Comsys-whz
 * @date 2013-3-21 上午8:59:36
 * @version V1.0
 */

package com.topinfo.model;

/**
 * @ClassName: ServerIp
 * @Description: TODO
 * @author tyler.wu-whz
 * @date 2013-3-21 上午8:59:36
 *
 */

public class ServerIp {

    private int    localGrade;

    private String ip;

    /**
     * getter method
     * @return the localGrade
     */

    public int getLocalGrade(){
        return localGrade;
    }

    /**
     * setter method
     * @param localGrade the localGrade to set
     */

    public void setLocalGrade(int localGrade){
        this.localGrade = localGrade;
    }

    /**
     * getter method
     * @return the ip
     */

    public String getIp(){
        return ip;
    }

    /**
     * setter method
     * @param ip the ip to set
     */

    public void setIp(String ip){
        this.ip = ip;
    }

}
