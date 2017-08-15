package cn.myloveqian.petInfo;

import java.sql.*;

/**
 * Created by zhiwei on 17-8-15.
 */
public class EditUtils {

    private static String querySql = "SELECT t.disease_photo,t.sample_id FROM td_disease_sample t WHERE t.disease_photo IS NOT NULL";

    private static String querySqlSample = "SELECT t.photo,t.id FROM td_disease t WHERE t.photo IS NOT NULL";

    private static String insertSql = "UPDATE td_disease_sample t SET t.disease_photo=? WHERE t.sample_id=?";

    private static String insertSqlSample = "UPDATE td_disease t SET t.photo=? WHERE t.id=?";

    public static void main(String[] args) throws SQLException {
        MysqlUtils utils = new MysqlUtils();
        Connection mysql = utils.getMysql();
        Statement statement = mysql.createStatement();
        ResultSet resultSet = statement.executeQuery(querySqlSample);
        PreparedStatement preparedStatement = mysql.prepareStatement(insertSqlSample);
        while (resultSet.next()) {
            String diseasePhoto = resultSet.getString(1);
            String sampleId = resultSet.getString(2);
//            String[] split = diseasePhoto.split(",");
//            StringBuilder sb = new StringBuilder();
//            for (String key : split) {
//                String replace = "";
//                if (key.contains("http://www.zgcwdy.com/Public/Uploads/logo/")) {
//                    replace = key.replace("http://www.zgcwdy.com/Public/Uploads/logo/", "");
//                }
//                sb.append(replace).append(",");
//            }
//            String s = sb.toString();

            String replace = diseasePhoto.replace("http://www.zgcwdy.com/Public/Uploads/logo/", "");
//            preparedStatement.setString(1, s.substring(0, s.length() - 1));
            preparedStatement.setString(1, replace);
            preparedStatement.setString(2, sampleId);
            preparedStatement.addBatch();
        }
        preparedStatement.executeBatch();

        preparedStatement.closeOnCompletion();
        statement.closeOnCompletion();
        mysql.close();
    }
}
