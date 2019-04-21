package com.jerry.moneyapp.bean;

import java.util.concurrent.ConcurrentHashMap;


/**
 * 将所有创建的表格相同的部分封装到这个BaseDao中
 * Created by wzl on 2018/9/26.
 */
public class BaseDao {

    private static final String DB_TJ = "tj.db";
    private static ConcurrentHashMap<String, DaoManager> managerMap = new ConcurrentHashMap<>();

    public static DaoManager getTjDb() {
        return getDb(DB_TJ);
    }

    private static DaoManager getDb(String db) {
        if (!managerMap.containsKey(db) || managerMap.get(db) == null) {
            managerMap.put(db, new DaoManager(db));
        }
        return managerMap.get(db);
    }
}
