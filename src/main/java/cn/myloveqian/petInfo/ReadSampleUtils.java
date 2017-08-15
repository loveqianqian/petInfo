package cn.myloveqian.petInfo;


import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * cn.myloveqian.petInfo
 *
 * @author zhiwei
 * @create 2017-08-15 16:46.
 * @github {@https://github.com/loveqianqian}
 */
public class ReadSampleUtils {

    private static String url = "http://www.zgcwdy.com";

    private static String insertSql = "INSERT INTO td_disease_sample(disease_type, type, title, disease_describe, disease_photo, zhengzhuang, causes, plan, recommend, tip) VALUES (?,?,?,?,?,?,?,?,?,?)";

    private static String querySql = "SELECT t.disease_type,t.type,t.rediectUrl FROM td_disease t";

    public static void main(String[] args) throws SQLException, IOException {
        MysqlUtils mysqlUtils = new MysqlUtils();
        java.sql.Connection mysql = mysqlUtils.getMysql();
        Statement statement = mysql.createStatement();
        ResultSet resultSet = statement.executeQuery(querySql);
        List<Map<String, String>> resultList = new ArrayList<>();
        while (resultSet.next()) {
            Map<String, String> resultMap = new HashMap<>();
            String title = "";
            String diseaseDescribe = "";
            String diseasePhoto = "";
            String zhengzhuang = "";
            String causes = "";
            String plan = "";
            String recommend = "";
            String tip = "";
            String diseaseType = resultSet.getString(1);
            resultMap.put("diseaseType", diseaseType);
            String type = resultSet.getString(2);
            resultMap.put("type", type);
            String rediectUrl = resultSet.getString(3);
            Connection totConn = Jsoup.connect(rediectUrl);
            Connection.Response execute = totConn.execute();
            String body = execute.body();
            Document document = Jsoup.parseBodyFragment(body);
            title = document.select("p.a_tit").text();
            resultMap.put("title", title);
            Elements ss = document.select("div.txt_con");
            for (Element s : ss) {
                String text = s.select("div.d_tit").text();
                String content = s.select("div.d_con").text();
                if (text.equals("疾病描述")) {
                    diseaseDescribe = content;
                } else if (text.equals("疾病图片")) {
                    Elements imgs = s.select("li").select("img");
                    StringBuilder sb = new StringBuilder();
                    for (Element img : imgs) {
                        String src = img.attr("src");
                        sb.append(url).append(src).append(",");
                    }
                    diseasePhoto = sb.toString();
                } else if (text.equals("症状")) {
                    zhengzhuang = content;
                } else if (text.equals("病因")) {
                    causes = content;
                } else if (text.equals("治疗方案")) {
                    plan = content;
                }
            }
            Elements ps = document.select("blockquote.wxqq-bg").select("p");
            for (Element p : ps) {
                String pText = p.text();
                if (pText.contains("推荐检查")) {
                    recommend = pText.replace("推荐检查:", "");
                } else if (pText.contains("导医提示")) {
                    tip = pText.replace("导医提示:", "");
                }
            }
            resultMap.put("zhengzhuang", zhengzhuang);
            resultMap.put("diseasePhoto", diseasePhoto);
            resultMap.put("diseaseDescribe", diseaseDescribe);
            resultMap.put("causes", causes);
            resultMap.put("plan", plan);
            resultMap.put("tip", tip);
            resultMap.put("recommend", recommend);
            resultList.add(resultMap);
        }
        PreparedStatement preparedStatement = mysql.prepareStatement(insertSql);
        for (Map<String, String> temp : resultList) {
            preparedStatement.setString(1, temp.get("diseaseType").trim());
            preparedStatement.setString(2, temp.get("type").trim());
            preparedStatement.setString(3, temp.get("title").trim());
            preparedStatement.setString(4, temp.get("diseaseDescribe").trim());
            preparedStatement.setString(5, temp.get("diseasePhoto").trim());
            preparedStatement.setString(6, temp.get("zhengzhuang").trim());
            preparedStatement.setString(7, temp.get("causes").trim());
            preparedStatement.setString(8, temp.get("plan").trim());
            preparedStatement.setString(9, temp.get("recommend").trim());
            preparedStatement.setString(10, temp.get("tip").trim());
            preparedStatement.addBatch();
        }
        preparedStatement.executeBatch();
        mysql.close();
    }
}
