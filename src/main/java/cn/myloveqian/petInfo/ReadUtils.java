package cn.myloveqian.petInfo;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
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
 * @create 2017-08-15 9:49.
 * @github {@https://github.com/loveqianqian}
 */
public class ReadUtils {

    private static String url = "http://www.zgcwdy.com";

    private static String insertSql = "INSERT INTO td_disease (photo,type,rediectUrl,disease_desc,disease_type) VALUES (?,?,?,?,?)";

    private static int totalNum = 0;

    public static void main(String[] args) throws SQLException {
        MysqlUtils utils = new MysqlUtils();
        java.sql.Connection jdbcConnection = utils.getMysql();
        PreparedStatement statement = jdbcConnection.prepareStatement(insertSql);
        List<Map<String, String>> list = new ArrayList<>();
        Connection totConn = Jsoup.connect("http://www.zgcwdy.com/index.php?s=/Health/jibing/tid/3");
        Connection.Response totExecute = null;
        try {
            totExecute = totConn.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Elements totEles = Jsoup.parseBodyFragment(totExecute.body()).select("div.list_ks").select("ul").select("a");
        for (Element totEle : totEles) {
            String href = totEle.attr("href");
            String typeName = totEle.select("li").text();
            Connection connect = Jsoup.connect(url + href);
            Connection.Response execute = null;
            try {
                execute = connect.execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String body = execute.body();
            Document document = Jsoup.parse(body);
            String total = document
                    .select("div.f_p_r")
                    .select("li")
                    .select("span")
                    .select("font")
                    .select("b").text();
            int totalInt = Integer.parseInt(total);
            int num = totalInt / 6 + 1;
            Element element = document.select("div.f_p_r").select("li>a").get(0);
            String h = url + element.attr("href");
            String real = h.substring(0, h.length() - 1);
            for (int i = 0; i <= num; i++) {
                Map<String, String> map = new HashMap<>();
                map.put("id", i + "");
                map.put("href", real + (i + 1));
                map.put("typeName", typeName);
                list.add(map);
            }
        }

        for (Map<String, String> temp : list) {
            getContent(statement, temp.get("href"), temp.get("typeName"));
        }

        statement.executeBatch();
        utils.closeConn();
        System.out.println(totalNum);
    }

    private static void getContent(PreparedStatement statement, String href, String typeName) throws SQLException {
        System.out.println(href);
        Connection connect = Jsoup.connect(href);
        Connection.Response execute = null;
        try {
            execute = connect.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String body = execute.body();
        Document document = Jsoup.parseBodyFragment(body);
        Elements eles = document.select("div.a_art");
        for (Element ele : eles) {
            totalNum++;
            String src = ele.select("div.a_con_l").select("img").attr("src");
            statement.setString(1, url + src);
            Elements aEle = ele.select("div.a_con_r").select("div.a_c_top").select("a");
            String newUrl = aEle.attr("href");
            String text = aEle.text();
            statement.setString(2, text);
            statement.setString(3, url + "/" + newUrl);
            String content = ele.select("div.a_c_mid").text();
            statement.setString(4, content);
            statement.setString(5, typeName);
            statement.addBatch();
        }
    }

}
