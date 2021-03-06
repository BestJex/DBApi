package com.jq.dbapi.util;

import com.alibaba.druid.util.JdbcConstants;
import com.jq.dbapi.domain.DataSource;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;

@Slf4j
public class JdbcUtil {

    public static ResultSet query(String sql, Connection connection) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();
        return resultSet;
    }

    public static Connection getConnection(DataSource ds) throws SQLException, ClassNotFoundException {
        String url = ds.getUrl();
        switch (ds.getType()) {
            case JdbcConstants.MYSQL:
                Class.forName("com.mysql.jdbc.Driver");
                break;
            case JdbcConstants.POSTGRESQL:
                Class.forName("org.postgresql.Driver");
                break;
            case JdbcConstants.HIVE:
                Class.forName("org.apache.hive.jdbc.HiveDriver");
                break;
            case JdbcConstants.SQL_SERVER:
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                break;
            default:
                break;
        }

        Connection connection = DriverManager.getConnection(url, ds.getUsername(), ds.getPassword());
        log.info("获取连接成功");
        return connection;
    }

}