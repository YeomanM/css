package com.demo;

import org.apache.fontbox.ttf.GlyphData;
import org.apache.fontbox.ttf.GlyphRenderer;
import org.apache.fontbox.ttf.TTFParser;
import org.apache.fontbox.ttf.TrueTypeFont;

import java.io.*;
import java.util.*;
import java.util.zip.DataFormatException;

/**
 * @author 冯宇明
 * @version 1.0
 * @date 2020/1/3
 * @desc
 */
public class ConvertTTF2Points {



    public static Map<String,List<GlyphRenderer.Point>> getUniMap2Points(String ttfPathName) throws IOException {
        return ConvertTTF2Points.getUniMap2Points(new FileInputStream(ttfPathName));
    }

    /**
     * 提取输入的ttf文件流中的unicode字符及对应的坐标点
     * @param is ttf文件流
     * @return unicode字符对应的坐标点
     * @throws IOException
     */
    public static Map<String,List<GlyphRenderer.Point>> getUniMap2Points(InputStream is) throws IOException {
        TTFParser parser = new TTFParser(true);

        //使用fontbox获取
        TrueTypeFont ttf = parser.parse(is);
        Map<String,List<GlyphRenderer.Point>> result = new HashMap<>(16);
        if (ttf.getGlyph() != null && ttf.getGlyph().getGlyphs().length > 0) {
            GlyphData[] data = ttf.getGlyph().getGlyphs();
            GlyphRenderer renderer = new GlyphRenderer(null);
            String[] unicodes = ttf.getPostScript().getGlyphNames();
            for (int i = 0; i < data.length; i++) {
                if (data[i] == null || data[i].getDescription() == null) {
                    continue;
                }
                result.put(unicodes[i], renderer.describeWithoutNotDraw(data[i].getDescription()));
            }
        }
        return result;
    }

    public static Map<String,String> getUniMap2Value(String templatePath,String ttfPathName) throws IOException {
        return getUniMap2Value(templatePath, ConvertTTF2Points.getUniMap2Points(ttfPathName));
    }

    public static Map<String,String> getUniMap2Value(String templatePath,InputStream ttfPathName) throws IOException {
        return getUniMap2Value(templatePath, ConvertTTF2Points.getUniMap2Points(ttfPathName));
    }

    /**
     * 根据模板文件，获取unicode对应的值
     * @param templatePath 包含unicode对应的值及坐标的xml文件
     * @param map 需要解析的unicode及其对应的坐标
     * @return
     */
    public static Map<String,String> getUniMap2Value(String templatePath,Map<String,List<GlyphRenderer.Point>> map){

        map.forEach((k,v) -> {
            ConvertTTF2Points.clear(v);
        });

        GlyphXMLHandler xmlHolder = new GlyphXMLHandler(templatePath);
        Map<String,String> result = new HashMap<>(map.size());
        Map<String,String> unicodeMap2Value = ConvertTTF2Points.getTemplateUniMap2Value(xmlHolder);
        Map<String,List<GlyphRenderer.Point>> unicodeMap2Point = xmlHolder.getMap(XPath.TTGlyph.xpath,
                XPath.pt.xpath, XPath.pt.key, XPath.pt.value, "on");

        //对应算法，获取在误差范围内的相似点最多的模板文件中的unicode对应的值
        map.forEach((k,v) -> {
            int max = 0;
            String maxUni = "";
            for (Map.Entry<String, List<GlyphRenderer.Point>> entry : unicodeMap2Point.entrySet()) {
                int count = 0;
                if (v.size() > entry.getValue().size()) {
                    count = ConvertTTF2Points.getLikeCount(entry.getValue(), v);
                } else {
                    count = ConvertTTF2Points.getLikeCount(v, entry.getValue());
                }
                if (count > max) {
                    max = count;
                    maxUni = entry.getKey();
                }
            }
            result.put(k.toLowerCase(), unicodeMap2Value.get(maxUni));
        });
        return result;
    }

    private static Map<Integer,List<String>> pointCountMap2Uni(Map<String,List<GlyphRenderer.Point>> map) {
        Map<Integer, List<String>> result = new HashMap<>(map.size());
        map.forEach((k,v) -> {
            int length = v.size();
            if (!result.containsKey(length)) {
                List<String> list = new ArrayList<>();
                result.put(length,list);
            }
            result.get(length).add(k);
        });
        return result;
    }

    /**
     * 获取模板xml文件内的unicode及对应的值
     * @param xmlHolder
     * @return
     */
    private static Map<String,String> getTemplateUniMap2Value(GlyphXMLHandler xmlHolder) {
        Map<String,String> map = xmlHolder.getMap(XPath.UnicodeMap2Value.xpath, XPath.UnicodeMap2Value.key, XPath.UnicodeMap2Value.value);

        if(map.isEmpty()) {
            Map<String,String> glyph = xmlHolder.getMap(XPath.GlyphIdMap2Value.xpath, XPath.GlyphIdMap2Value.key, XPath.GlyphIdMap2Value.value);
            Map<String,String> unicode = xmlHolder.getMap(XPath.IndexMap2Value.xpath, XPath.IndexMap2Value.key, XPath.IndexMap2Value.value);
            unicode.forEach((key, value) -> {
                map.put(value, glyph.get(key));
            });
        }
        return map;
    }

    /**
     * 两个unicode对应的坐标集在误差范围内的坐标的个数
     * @param points 较短的坐标集
     * @param longPoint 较多的坐标集
     * @return
     */
    private static int getLikeCount(List<GlyphRenderer.Point> points, List<GlyphRenderer.Point> longPoint) {
        int x = 0,lx = 0,len = points.size(),count = 0,llen = longPoint.size(),temp = 0,normal = 0;

        try {
            for (x = 0;x < len && lx < llen; x++,lx++) {

                //判断误差
                if (points.get(x).like(longPoint.get(lx), 20)){
                    count++;
                    normal = 0;
                } else {

                    //允许在连续的五个坐标点某个坐标点与另一个坐标集的点误差计算
                    if (normal == 1) {
                        x--;
                        temp++;
                        if (temp == 5) {
                            temp = 0;
                            lx -= 5;
                            normal = 2;
                        }
                    } else if(normal == 2){
                        lx--;
                        temp++;
                        if (temp == 5) {
                            temp = 0;
                            x -= 5;
                            normal = 3;
                        }
                    } else {
                        x++;
                        normal = 0;
                    }
                    normal = normal == 0 ? 1 : normal;

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return count;
    }

    /**
     * 清除不用描述的点
     * @param points
     */
    private static void clear(List<GlyphRenderer.Point> points) {
        Iterator<GlyphRenderer.Point> iterator = points.iterator();
        GlyphRenderer.Point point;
        while (iterator.hasNext()) {
            point = iterator.next();
            if (!point.isOnCurve()) {
                iterator.remove();
            }
        }
    }

    enum XPath {
        UnicodeMap2Value("/ttFont/UnicodeMap2Value/Map", "unicode", "value"),
        GlyphIdMap2Value("/ttFont/GlyphOrder/GlyphID", "id", "value"),
        IndexMap2Value("/ttFont/IndexMap2Value/Map", "index", "value"),
        pt("/contour/pt", "x", "y"),
        TTGlyph("/ttFont/glyf/TTGlyph", "x", "y"),
        ;

        private String xpath;
        private String key;
        private String value;

        XPath(String xpath,String key,String value) {
            this.xpath = xpath;
            this.key = key;
            this.value = value;
        }
    }

    public static void main(String[] args) throws IOException, DataFormatException {
//        Map<String,List<GlyphRenderer.Point>> map = ConvertTTF2Points.getUniMap2Points("G:\\JavaWeb\\JetBrains\\css\\c2.ttf");
        String xml = "woff1.xml";
        Convert2TTF.woffToTTF("F:/soft/java/JetBrains/project/css/src/main/resources/woff/test1.woff",
                "F:\\soft\\java\\JetBrains\\project\\css\\src\\main\\resources\\woff\\ttf10.ttf");
        Convert2TTF.ConvertTTF2XML("F:\\soft\\java\\JetBrains\\project\\css\\woff1.ttf", xml);
//        Map<String,String> result = ConvertTTF2Points.getUniMap2Value(xml, map);
//        System.out.println(result);
//        JSONObject object = (JSONObject) JSONObject.toJSON(map);
//        System.out.println(JSON.toJSONString(result));
    }



}
