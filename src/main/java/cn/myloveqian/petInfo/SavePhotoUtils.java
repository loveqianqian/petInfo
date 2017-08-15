package cn.myloveqian.petInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by zhiwei on 17-8-15.
 */
public class SavePhotoUtils {

    private static String queryDisease = "SELECT t.photo FROM td_disease t";

    private static String queryDiseaseSample = "SELECT t.disease_photo FROM td_disease_sample t";

    private static String totPath = "/home/zhiwei/upload/";

    public static void main(String[] args) throws SQLException {
        MysqlUtils mysqlUtils = new MysqlUtils();
        Connection mysql = mysqlUtils.getMysql();
        Statement statement = mysql.createStatement();
        ResultSet resultSet = statement.executeQuery(queryDisease);
        while (resultSet.next()) {
            String imgUrl = resultSet.getString(1);
            getImg(imgUrl);
        }
        ResultSet rs2 = statement.executeQuery(queryDiseaseSample);
        while (rs2.next()) {
            String imgUrls = rs2.getString(1);
            String[] split = imgUrls.split(",");
            for (String key : split) {
                if (!key.equals("")) {
                    getImg(key);
                }
            }
        }
    }

    private static void getImg(String imgUrl) {
        String[] split = imgUrl.split("/");
        String fileName = split[split.length - 1];
        try {
            File files = new File(totPath);
            if (!files.exists()) {
                files.mkdirs();
            }
            URL url = new URL(imgUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            InputStream is = connection.getInputStream();
            File file = new File(totPath + fileName);
            FileOutputStream out = new FileOutputStream(file);
            int i;
            while ((i = is.read()) != -1) {
                out.write(i);
            }
            out.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
