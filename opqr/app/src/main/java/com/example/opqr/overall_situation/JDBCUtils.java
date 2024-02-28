package com.example.opqr.overall_situation;


import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * function： 数据库工具类，连接数据库用
 */


public class JDBCUtils {
    private static final String TAG = "mysql-party-JDBCUtils";

    private static String driver = "com.mysql.jdbc.Driver";// MySql驱动

    private static String dbName = "test";// 数据库名称

    private static String user = "appop";// 用户名

    private static String password = "12345678";// 密码

    public static Connection getConn(){

        Connection connection = null;
        try{
            Class.forName(driver);// 动态加载类
           // String ip = NetWorkUtils.getLocalIpAddress(view.getContext());// 写成本机地址，不能写成localhost，同时手机和电脑连接的网络必须是同一个
            // 尝试建立到给定数据库URL的连接
            connection = DriverManager.getConnection("jdbc:mysql://" + "8.130.42.232" + ":3306/" + dbName+"?useSSL=false",
                    user, password);

        }catch (Exception e){
            e.printStackTrace();
            Log.e(TAG,Log.getStackTraceString(e));
        }
        return connection;
    }
}
