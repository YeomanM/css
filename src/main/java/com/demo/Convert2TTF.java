package com.demo;

import com.demo.utils.FileUtil;
import com.demo.woff.WoffConverter;
import org.apache.fontbox.ttf.GlyphRenderer;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.zip.DataFormatException;

/**
 * @author 冯宇明
 * @version 1.0
 * @date 2020/1/3
 * @desc
 */
public class Convert2TTF {

    public static void base64ToTTF(String base64Str, String ttfPathName) throws IOException, DataFormatException {
        byte[] ss = Base64.getDecoder().decode(base64Str);
        WoffConverter w = new WoffConverter();
        InputStream is = new ByteArrayInputStream(ss);
        ss = w.convertToTTFByteArray(is);
        FileUtil.write(ttfPathName, ss);
        is.close();
    }

    public static InputStream base64ToTTFStream(String base64Str) throws IOException, DataFormatException {
        byte[] ss = Base64.getDecoder().decode(base64Str);
        WoffConverter w = new WoffConverter();
        InputStream is = new ByteArrayInputStream(ss);
        ss = w.convertToTTFByteArray(is);
        InputStream inputStream = new ByteArrayInputStream(ss);
        is.close();
        return inputStream;
    }

    public static InputStream woffToTTFStream(String woffFilePath) throws IOException, DataFormatException {
        WoffConverter w = new WoffConverter();
        InputStream is = new FileInputStream(woffFilePath);
        byte[] ss = w.convertToTTFByteArray(is);
        InputStream inputStream = new ByteArrayInputStream(ss);
        is.close();
        return inputStream;
    }

    public static void woffToTTF(String woffFilePath, String ttfPathName) throws IOException, DataFormatException {
        WoffConverter w = new WoffConverter();
        InputStream is = new FileInputStream(woffFilePath);
        byte[] ss = w.convertToTTFByteArray(is);
        FileUtil.write(ttfPathName, ss);
        is.close();
    }

    public static void main(String[] args) throws IOException, DataFormatException {
        String path = "G:\\python\\project\\learn\\c1.woff";
        Convert2TTF.woffToTTF(path,"./c1.ttf");
    }

    public static void ConvertTTF2XML(String ttfPathName, String xmlPath) throws IOException {
        Map<String, List<GlyphRenderer.Point>> points = ConvertTTF2Points.getUniMap2Points(ttfPathName);

        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("ttFont");
        Element glyf = root.addElement("glyf");
        Element UnicodeMap2Value = root.addElement("UnicodeMap2Value");

        points.forEach((key, value) -> {
            Element TTGlyph = glyf.addElement("TTGlyph");
            TTGlyph.addAttribute("name", key);
            Element contour = TTGlyph.addElement("contour");
            Element map = UnicodeMap2Value.addElement("Map");
            map.addAttribute("unicode", key);
            map.addAttribute("value", "");
            for (GlyphRenderer.Point point : value) {
                Element pt = contour.addElement("pt");
                pt.addAttribute("x", String.valueOf(point.getX()));
                pt.addAttribute("y", String.valueOf(point.getY()));
                pt.addAttribute("on", point.isOnCurve() ? "1" : "0");
            }
        });

        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("utf-8");
        File file = new File(xmlPath);

        XMLWriter writer = new XMLWriter(new FileOutputStream(file), format);
        writer.setEscapeText(false);
        writer.write(document);
        writer.close();
    }
}
