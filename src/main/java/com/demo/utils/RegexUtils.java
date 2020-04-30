package com.demo.utils;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils {
	private static final String HTML_TAG_REGEX = "<[^>]+>"; // 定义HTML标签的正则表达式

	public static String extractData(String content, String p) {
		return extractData(content, p, 1);
	}

	public static String extractData(String content, String p, int group) {
		Pattern pattern = Pattern.compile(p, Pattern.DOTALL);
		Matcher m = pattern.matcher(content);

		if (m.find()) {
			return m.group(group).trim();
		}

		return "";
	}

	public static List<String> extractDatas(String content, String p) {
		return extractDatas(content, p, 1);
	}

	public static List<String> extractDatas(String content, String p,  int group) {
		Pattern pattern = Pattern.compile(p, Pattern.DOTALL);
		Matcher m = pattern.matcher(content);
		List<String> list = new ArrayList<String>();

		while (m.find()) {
			list.add(m.group(group).trim());
		}

		return list;
	}

	public static int extractNumberData(String content, String p) {
		String strValue = extractData(content, p);
		int value = 0;

		try {
			value = Integer.parseInt(strValue);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return value;
	}

	public static boolean matches(String regex, String content) {
		Pattern p = Pattern.compile(regex, Pattern.DOTALL); // 匹配换行符
		Matcher matcher = p.matcher(content);
		return matcher.matches();
	}

	public static String removeHtmlTag(String content) {
		Pattern p_html = Pattern.compile(HTML_TAG_REGEX, Pattern.DOTALL);
		Matcher m_html = p_html.matcher(content);
		return m_html.replaceAll("").trim(); // 过滤html标签
	}

	public static String removeScripts(String content) {
		Pattern pattern = Pattern.compile("<script.*?>.*?</script>", Pattern.DOTALL);
		Matcher matcher = pattern.matcher(content);
		return matcher.replaceAll("").trim();
	}

	public static String getParagraphText(String content) {
		StringBuilder sb = new StringBuilder();

		Pattern pattern = Pattern.compile("<p>(.+?)</p>", Pattern.DOTALL);
		Pattern p_html = Pattern.compile(HTML_TAG_REGEX, Pattern.DOTALL);

		Matcher m = pattern.matcher(content);

		while (m.find()) {
			Matcher result = p_html.matcher(m.group(1).trim());
			sb.append(result.replaceAll("").trim());
		}

		return sb.toString();
	}

	public static void main(String[] args) {
		String content = "hello world<script naa=sfdsafdsf>wasfdnadfsdf adfd assf     </script><script naa=sfdsafdsf>wasfdnadfsdf adfd assf     </script><script naa=sfdsafdsf>wasfdnadfsdf adfd assf     </script>";
		System.out.println(removeScripts(content));
		String table = "crmbp1.test";
		System.out.println(matches("(mallerpcrm|mallorder|crmbp)\\..*", table));
	}

}
