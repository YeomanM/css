package com.demo;

import com.demo.utils.HttpAccessor;
import com.demo.utils.RegexUtils;
import net.minidev.json.JSONUtil;
import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import us.codecraft.webmagic.selector.CssSelector;
import us.codecraft.webmagic.selector.Html;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;


public class ProgrammeByTemplate {

    public static void main(String[] args) throws Exception {

        String http = HttpAccessor.doAttemptHttpGet("http://127.0.0.1:8848/test/list.html");
        Html html = new Html(http);
        String base64 = html.selectDocument(new CssSelector("#js-nuwa"));
        base64 = RegexUtils.extractData(base64, "base64,([a-zA-Z0-9/+=]+)\\)");
        InputStream is = Convert2TTF.base64ToTTFStream(base64);


        List<String> list = RegexUtils.extractDatas(http, "<li class=\"col2 tr\"><i class=\"cs\">([&#;a-zA-Z\\d]+)</i>");
        System.out.println(list.size());

        Map<String,String> result = ConvertTTF2Points.getUniMap2Value("temp.xml", ConvertTTF2Points.getUniMap2Points(is));
        for (int i = 0; i < 10; i++) {
            String data = list.get(i).toLowerCase();
            if (data.contains("&#x")) {
                data = data.replaceAll("&#x", "uni");
            }

            String[] datas = data.split(";");
            StringBuilder builder = new StringBuilder();
            for (String d : datas) {
                builder.append(result.getOrDefault(d, ""));
            }
            System.out.println(i + " : " + builder.toString());
        }
    }

}
