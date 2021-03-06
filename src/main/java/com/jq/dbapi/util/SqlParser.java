package com.jq.dbapi.util;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.hive.visitor.HiveSchemaStatVisitor;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGSchemaStatVisitor;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerSchemaStatVisitor;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: dolphinscheduler
 * @description:
 * @author: jiangqiang
 * @create: 2020-08-31 17:28
 **/
@Slf4j
public class SqlParser {
    private static final Logger logger = LoggerFactory.getLogger(SqlParser.class);

    /**
     * 1是查询类sql; 2是非查询类sql
     * @param sql
     * @param dbType
     * @return
     */
    public static Integer isSelect(String sql, String dbType) {

        try {
            List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
            SQLStatement stmt = stmtList.get(0);
            SQLSelectStatement sqlSelectStatement = (SQLSelectStatement) stmt;
            return 1;
        } catch (Exception e) {
            log.error(e.getMessage());
            return 0;
        }

    }

//    public static void getLimit(String sql) {
//        String dbType = JdbcConstants.MYSQL;
//
//        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
//
//        SQLStatement stmt = stmtList.get(0);
//
//
//        SQLSelectStatement sqlSelectStatement = (SQLSelectStatement) stmt;
//        SQLSelectQuery sqlSelectQuery = sqlSelectStatement.getSelect().getQuery();
//
//        SQLSelectQueryBlock sqlSelectQueryBlock = (SQLSelectQueryBlock) sqlSelectQuery;
//        SQLLimit limit = sqlSelectQueryBlock.getLimit();
//        System.out.println(limit.getRowCount());
//    }

//    public static List<String> getResponseParam(String sql) {
//        String dbType = JdbcConstants.MYSQL;
//
//        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
//
//        SQLStatement stmt = stmtList.get(0);
//
//        List<SQLSelectItem> list = ((MySqlSelectQueryBlock) ((SQLSelect) ((SQLSelectStatement) stmt).getSelect()).getQuery()).getSelectList();
//
//        List<String> collect = list.stream().map(t -> t.computeAlias()).collect(Collectors.toList());
//
//        return collect;
//    }

    public static SchemaStatVisitor getSQLASTVisitor(String type) {
        SchemaStatVisitor visitor = new MySqlSchemaStatVisitor();

        switch (type) {
            case JdbcConstants.POSTGRESQL:
                visitor = new PGSchemaStatVisitor();
                break;
            case JdbcConstants.MYSQL:
            case JdbcConstants.MARIADB:
                visitor = new MySqlSchemaStatVisitor();
                break;
            case JdbcConstants.HIVE:
                visitor = new HiveSchemaStatVisitor();
                break;
            case JdbcConstants.SQL_SERVER:
                visitor = new SQLServerSchemaStatVisitor();
                break;
            case JdbcConstants.ORACLE:
                visitor = new OracleSchemaStatVisitor();
                break;

        }
        return visitor;

    }

    public static List<String> getRequestParam(String sql, String dbType) {

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);

        SQLStatement stmt = stmtList.get(0);

        SchemaStatVisitor visitor = getSQLASTVisitor(dbType);
        stmt.accept(visitor);

        Collection<TableStat.Column> columns = visitor.getColumns();
        List<String> list = columns.stream().filter(t -> {

            return t.getName().startsWith("$");
        })
                .map(t -> t.getName().substring(1)).collect(Collectors.toList());

        return list;
    }

    // 解析sql的表名
//    public static List<String> getTableNameBySql(String sql) {
//        String dbType = JdbcConstants.MYSQL;
////        switch (type) {
////            case "MYSQL":
////                dbType = JdbcConstants.MYSQL;
////                break;
////            case "POSTGRESQL":
////                dbType = JdbcConstants.POSTGRESQL;
////                break;
////            case "HIVE":
////                dbType = JdbcConstants.HIVE;
////                break;
////            default:
////        }
//        try {
//            List<String> tableNameList = new ArrayList<>();
//            //格式化输出
//            String sqlResult = SQLUtils.format(sql, dbType);
//            logger.info("格式化后的sql:[{}]", sqlResult);
//
//            List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
//            if (CollectionUtils.isEmpty(stmtList)) {
//                logger.info("stmtList为空无需获取");
//                return Collections.emptyList();
//            }
//            for (SQLStatement sqlStatement : stmtList) {
//                MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
//                sqlStatement.accept(visitor);
//                Map<TableStat.Name, TableStat> tables = visitor.getTables();
//                logger.info("druid解析sql的结果集:[{}]", tables);
//                Set<TableStat.Name> tableNameSet = tables.keySet();
//                for (TableStat.Name name : tableNameSet) {
//                    String tableName = name.getName();
//                    if (StringUtils.isNotBlank(tableName)) {
//                        tableNameList.add(tableName);
//                    }
//                }
//            }
//            logger.info("解析sql后的表名:[{}]", tableNameList);
//            return tableNameList;
//        } catch (Exception e) {
//            logger.error("**************异常SQL:[{}]*****************\\n", sql);
//            logger.error(e.getMessage(), e);
//        }
//        return Collections.emptyList();
//    }

    public static void main(String[] args) throws IOException {
//        String sql = FileUtils.readFileToString(new File("d:/sql.sql"), "utf-8");
//        List<String> requestParam = getRequestParam(sql);
//        System.out.println(JSON.toJSONString(requestParam));
    }
}

