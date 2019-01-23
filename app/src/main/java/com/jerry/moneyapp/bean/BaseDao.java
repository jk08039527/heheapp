package com.jerry.moneyapp.bean;

import java.util.concurrent.ConcurrentHashMap;


/**
 * 将所有创建的表格相同的部分封装到这个BaseDao中
 * Created by wzl on 2018/9/26.
 */
public class BaseDao {

    private static final String LOCAL_DB = "local.db";
    private static ConcurrentHashMap<String, DaoManager> managerMap = new ConcurrentHashMap<>();

    public static DaoManager getTjDb() {
        return getDb();
    }

    private static DaoManager getDb() {
        if (!managerMap.containsKey(LOCAL_DB) || managerMap.get(LOCAL_DB) == null) {
            managerMap.put(LOCAL_DB, new DaoManager(LOCAL_DB));
        }
        return managerMap.get(LOCAL_DB);
    }
}
