package cn.myloveqian.petInfo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * cn.myloveqian.petInfo
 *
 * @author zhiwei
 * @create 2017-08-15 10:40.
 * @github {@https://github.com/loveqianqian}
 */
public class MysqlUtils {

    private Connection connection;

    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Connection getMysql() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection("jdbc:mysql://bj-cdb-65ds0oeb.sql.tencentcdb.com:63945/pet?characterEncoding=utf8", "root", "csdiao210");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }

    public void closeConn() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
