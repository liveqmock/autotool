/**
 * @Title: DefaultCommonDao.java
 * @Package com.topinfo.db.dao
 * @Description: TODO
 *               Company:图讯科技
 * @author Comsys-whz
 * @date 2013-3-15 上午11:02:44
 * @version V1.0
 */

package com.topinfo.db.dao;

import java.util.List;

import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.stereotype.Repository;
import com.topinfo.model.AutoConf;
import com.topinfo.model.AutoUpdate;
import com.topinfo.model.ServerIp;

/**
 * @ClassName: DefaultCommonDao
 * @Description: TODO
 * @author tyler.wu-whz
 * @date 2013-3-15 上午11:02:44
 *
 */
@Repository("commonDao")
public class DefaultCommonDao extends SqlMapClientTemplate {

    public void insertAppConf(AutoConf conf){
        this.insert ("insertAppConf", conf);
    }

    public List<AutoConf> getNeedApp(){
        return this.queryForList ("selectallapp", null);
    }

    public void insertAutoUpdateLog(AutoUpdate updateLog){
        AutoUpdate up = (AutoUpdate) this.queryForObject ("queryAutoUpdateByJgdm", updateLog);
        if (up == null) this.insert ("insertautoupdatelog", updateLog);
        else this.update ("updateautoupdatelog", updateLog);
    }

    public AutoConf getAppVersionByAppid(int appid){
        return (AutoConf) this.queryForObject ("getAppVersionByAppid", appid);
    }

    public void updateAppVersion(AutoConf autoConf){
        this.update ("updateAutoAppVersion", autoConf);
    }

    public List<AutoUpdate> getAllLog(){
        return this.queryForList ("getAllUpdateLog", null);
    }

}
