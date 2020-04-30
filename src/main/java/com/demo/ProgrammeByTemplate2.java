package com.demo;

import com.demo.utils.HttpAccessor;
import com.demo.utils.RegexUtils;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class ProgrammeByTemplate2 {

    public static void main(String[] args) throws Exception {

        //请求数据
        String http = HttpAccessor.doAttemptHttpGet("http://127.0.0.1:8848/test/test.html");
        //解析woff文件url
        String woffUrl = RegexUtils.extractData(http, "([/a-zA-Z0-9.]+)\\'\\)\\s*format\\(\\'woff\\'\\);");

        //下载文件
        File file = new File("");
        System.out.println(woffUrl);
        String woffPath = "/woff.woff";
        HttpAccessor.downloadFromUrl("https:" + woffUrl, file.getAbsolutePath(), woffPath);
        InputStream is = Convert2TTF.woffToTTFStream( file.getAbsolutePath() + woffPath);

        List<String> list = RegexUtils.extractDatas(http, "class=\"stonefont\">([&#;a-zA-Z\\d.]+)</span>");

        //揭西县
        Map<String,String> result = ConvertTTF2Points.getUniMap2Value("temp.xml", ConvertTTF2Points.getUniMap2Points(is));
        for (int i = 0; i < 10; i++) {
            String data = list.get(i).toLowerCase();
            if (data.contains("&#x")) {
                data = data.replaceAll("&#x", "uni");
            }

            String[] datas = data.split(";");
            StringBuilder builder = new StringBuilder();
            for (String d : datas) {
                if (d.startsWith(".")) {
                    d = d.substring(1);
                    builder.append(".");
                }
                builder.append(result.getOrDefault(d, ""));
            }
            System.out.println(i + " : " + builder.toString());
        }
    }

}
