package com.demo.utils;

public class OSUtils {
	public static String OSName = System.getProperty("os.name").toLowerCase();
	
	public static boolean isLinux() {
		return OSName.indexOf("linux") >= 0;
	}
	
	public static boolean isWindows() {
		return OSName.indexOf("windows") >= 0;
	}
}
