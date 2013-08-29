/**
 * @Title: AutoConf.java
 * @Package com.topinfo.model
 * @Description: TODO
 *               Company:图讯科技
 * @author Comsys-whz
 * @date 2013-3-13 下午4:39:55
 * @version V1.0
 */

package com.topinfo.model;

import java.util.Date;

/**
 * @ClassName: AutoConf
 * @Description: TODO
 * @author tyler.wu-whz
 * @date 2013-3-13 下午4:39:55
 *
 */

public class AutoConf {

    private int    appid;

    private String appName;

    private String appDb;

    private String appPath;

    private Date   createTime;

    private Date   updateTime;

    private int    version;

    /**
     * getter method
     * @return the version
     */

    public int getVersion(){
        return version;
    }

    /**
     * setter method
     * @param version the version to set
     */

    public void setVersion(int version){
        this.version = version;
    }

    /**
     * getter method
     * @return the appDb
     */

    public String getAppDb(){
        return appDb;
    }

    /**
     * setter method
     * @param appDb the appDb to set
     */

    public void setAppDb(String appDb){
        this.appDb = appDb;
    }

    /**
     * getter method
     * @return the appPath
     */

    public String getAppPath(){
        return appPath;
    }

    /**
     * setter method
     * @param appPath the appPath to set
     */

    public void setAppPath(String appPath){
        this.appPath = appPath;
    }

    /**
     * getter method
     * @return the createTime
     */

    public Date getCreateTime(){
        return createTime;
    }

    /**
     * setter method
     * @param createTime the createTime to set
     */

    public void setCreateTime(Date createTime){
        this.createTime = createTime;
    }

    /**
     * getter method
     * @return the updateTime
     */

    public Date getUpdateTime(){
        return updateTime;
    }

    /**
     * setter method
     * @param updateTime the updateTime to set
     */

    public void setUpdateTime(Date updateTime){
        this.updateTime = updateTime;
    }

    /**
     * getter method
     * @return the appid
     */

    public int getAppid(){
        return appid;
    }

    /**
     * setter method
     * @param appid the appid to set
     */

    public void setAppid(int appid){
        this.appid = appid;
    }

    /**
     * getter method
     * @return the appName
     */

    public String getAppName(){
        return appName;
    }

    /**
     * setter method
     * @param appName the appName to set
     */

    public void setAppName(String appName){
        this.appName = appName;
    }

}
