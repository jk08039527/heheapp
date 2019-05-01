package com.jerry.moneyapp.bean;

import com.jerry.moneyapp.BuildConfig;
import com.jerry.moneyapp.MyApplication;
import com.jerry.moneyapp.greendao.gen.DaoMaster;
import com.jerry.moneyapp.greendao.gen.DaoSession;
import com.jerry.moneyapp.util.LogUtils;

import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**
 * Created by wzl on 2018/9/26. 进行数据库的管理 1.创建数据库 2.创建数据库表 3.对数据库进行增删查改 4.对数据库进行升级
 */
public class DaoManager {

    private DaoMaster.DevOpenHelper mHelper;
    private DaoSession mDaoSession;

    private DaoManager() {
    }

    DaoManager(String dbName) {
        mHelper = new DaoMaster.DevOpenHelper(MyApplication.getInstances(), dbName, null);
        DaoMaster daoMaster = new DaoMaster(mHelper.getWritableDatabase());
        mDaoSession = daoMaster.newSession();
        setDebug();
    }

    /**
     * 设置debug模式开启或关闭，默认关闭
     */
    private void setDebug() {
        QueryBuilder.LOG_SQL = BuildConfig.DEBUG;
        QueryBuilder.LOG_VALUES = BuildConfig.DEBUG;
    }

    /**************************数据库插入操作***********************/
    /**
     * 插入单个对象
     */
    public boolean insertObject(Object object) {
        boolean flag = false;
        try {
            flag = mDaoSession.insert(object) != -1;
        } catch (Exception e) {
            LogUtils.e(e.toString());
        }
        return flag;
    }

    /**
     * 插入多个对象，并开启新的线程
     */
    public boolean insertMultObject(final List<?> objects) {
        boolean flag = false;
        try {
            if (objects != null && objects.size() > 0) {
                for (Object object : objects) {
                    mDaoSession.insertOrReplace(object);
                }
                flag = true;
            }
        } catch (Exception e) {
            LogUtils.e(e.toString());
        }
        return flag;
    }

    /**
     * 数据库删除操作 删除某个数据库表
     */
    public boolean deleteAll(Class<?> clss) {
        boolean flag;
        try {
            mDaoSession.deleteAll(clss);
            flag = true;
        } catch (Exception e) {
            LogUtils.e(e.toString());
            flag = false;
        }
        return flag;
    }

    /**
     * 数据库查询操作 获得某个表名
     */
    public String getTablename(Class<?> object) {
        return mDaoSession.getDao(object).getTablename();
    }

    /**
     * 查询某条件下的对象
     */
    public List<?> queryObject(Class<?> object, String where, String... params) {
        List<?> objects = null;
        try {
            Object obj = mDaoSession.getDao(object);
            if (null == obj) {
                return null;
            }
            objects = mDaoSession.getDao(object).queryRaw(where, params);
        } catch (Throwable e) {
            deleteAll(object);
            LogUtils.e(e.toString());
        }

        return objects;
    }

    /**
     * 查询所有对象
     */
    public <T> List<T> queryAll(Class<T> object, Property property) {
        List<T> objects = null;
        try {
            QueryBuilder<?> builder = mDaoSession.getDao(object).queryBuilder()
                    .orderDesc(property);
            objects = (List<T>) builder.list();
        } catch (Throwable e) {
            deleteAll(object);
            LogUtils.e(e.toString());
        }
        return objects;
    }

    /***************************关闭数据库*************************/
    /**
     * 关闭数据库一般在Odestory中使用
     */
    public void closeDataBase() {
        if (mHelper != null) {
            mHelper.close();
            mHelper = null;
        }
        if (null != mDaoSession) {
            mDaoSession.clear();
            mDaoSession = null;
        }
    }
}
