/**
 * @Title: AutoUpdate.java
 * @Package com.topinfo.model
 * @Description: TODO
 *               Company:图讯科技
 * @author Comsys-whz
 * @date 2013-3-13 下午4:40:34
 * @version V1.0
 */

package com.topinfo.model;

import java.util.Date;

/**
 * @ClassName: AutoUpdate
 * @Description: TODO
 * @author tyler.wu-whz
 * @date 2013-3-13 下午4:40:34
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         *
 */

public class AutoUpdate {

    private int    id;
    private int    appid;
    private int    version;
    private int    status;
    private String jgdm;
    private String ip;
    private String desc;
    private Date   createTime;
    private Date   updateTime;

    /**
     * getter method
     * @return the id
     */

    public int getId(){
        return id;
    }

    /**
     * setter method
     * @param id the id to set
     */

    public void setId(int id){
        this.id = id;
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
     * @return the status
     */

    public int getStatus(){
        return status;
    }

    /**
     * setter method
     * @param status the status to set
     */

    public void setStatus(int status){
        this.status = status;
    }

    /**
     * getter method
     * @return the jgdm
     */

    public String getJgdm(){
        return jgdm;
    }

    /**
     * setter method
     * @param jgdm the jgdm to set
     */

    public void setJgdm(String jgdm){
        this.jgdm = jgdm;
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

    /**
     * getter method
     * @return the desc
     */

    public String getDesc(){
        return desc;
    }

    /**
     * setter method
     * @param desc the desc to set
     */

    public void setDesc(String desc){
        this.desc = desc;
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

}
