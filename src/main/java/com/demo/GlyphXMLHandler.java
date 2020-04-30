package com.demo;

import org.apache.fontbox.ttf.GlyphRenderer;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 冯宇明
 * @version 1.0
 * @date 2020/1/6
 * @desc
 */
public class GlyphXMLHandler {

    private Element root;

    GlyphXMLHandler(String path) {
        SAXReader reader = new SAXReader();
        try {
            Document document = reader.read(path);
            root = document.getRootElement();
        } catch (DocumentException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    GlyphXMLHandler(InputStream inputStream) {
        SAXReader reader = new SAXReader();
        try {
            Document document = reader.read(inputStream);
            root = document.getRootElement();
        } catch (DocumentException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    Map<String,String> getMap(String xpath, String keyName, String valueName) {
        List<Element> elements = root.selectNodes(xpath);

        Map<String,String> map = new HashMap<>(24);
        elements.forEach(e -> {
            String key = e.attribute(keyName).getValue();
            String value = e.attribute(valueName).getValue();
            map.put(key, value);
        });
        return map;
    }

    public List<String> getMap(String xpath) {
        List<Element> elements = root.selectNodes(xpath);

        List<String> map = new ArrayList<>();
        elements.forEach(e -> {
            map.add(e.getText());
        });
        return map;
    }

    Map<String,List<GlyphRenderer.Point>> getMap(String pxpath, String cxpath, String keyName, String valueName, String on) {
        List<Element> elements = root.selectNodes(pxpath);

        Map<String,List<GlyphRenderer.Point>> map = new HashMap<>(24);
        elements.forEach(e -> {
            List<Integer> list = new ArrayList<>();
            List<Element> temp = e.elements(),children = new ArrayList<>();

            temp.forEach(t -> {
                children.addAll(t.elements());
            });

            String name = e.attribute("name").getValue();
            List<GlyphRenderer.Point> points = new ArrayList<>(children.size());
            map.put(name, points);
            Element child = null;
            for (int i = 0; i < children.size(); i++) {
                child = children.get(i);
                int o = Integer.parseInt(child.attribute(on).getValue());
                if (o != 0) {
                    int x = Integer.parseInt(child.attribute(keyName).getValue());
                    int y = Integer.parseInt(child.attribute(valueName).getValue());
                    points.add(new GlyphRenderer.Point(x, y));
                }
            }
        });
        return map;
    }

}
