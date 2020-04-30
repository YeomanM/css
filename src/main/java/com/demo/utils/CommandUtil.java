package com.demo.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @date 2020/04/07
 * @author 冯宇明
 * 执行命令行工具类
 */
public class CommandUtil {

    /**
     * 执行命令，linux下ps命令不能使用该方法返回结果
     * @param command 单行命令
     * @return 返回结果
     * @throws IOException 异常
     */
    public static List<String> run(String command) throws IOException {
        return exec(command);
    }

    /**
     * 执行命令，执行linux命令使用
     * @param command 多行命令
     * @return 返回结果
     * @throws IOException 异常
     */
    public static List<String> run(String[] command) throws IOException {
        return exec(command);
    }

    /**
     * 实际执行方法
     * @param command 命令
     * @return 返回结果
     * @throws IOException 异常
     */
    private static List<String> exec(Object command) throws IOException{

        Scanner input = null;
        List<String> result = new ArrayList<>();
        Process process = null;
        try {
            if (command.getClass() == String.class) {
                process = Runtime.getRuntime().exec((String) command);
            } else {
                process = Runtime.getRuntime().exec((String[]) command);
            }
            try {
                //等待命令执行完成
                process.waitFor(20, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            InputStream is = process.getInputStream();
            input = new Scanner(is);
            while (input.hasNextLine()) {
                result.add(input.nextLine());
            }
        } finally {
            if (input != null) {
                input.close();
            }
            if (process != null) {
                process.destroy();
            }
        }
        return result;
    }

    /**
     * 关闭火狐进程
     * @param ppid 启动火狐进程的pid
     * @throws IOException 异常
     */
    public static void killFirefox(String ppid) throws IOException {
        if (OSUtils.isWindows()) {
            killFirefoxInWindows(ppid);
        } else if (OSUtils.isLinux()) {
            killFirefoxInLinux(ppid);
        }
    }

    /**
     * 关闭火狐进程及启动火狐进程的进程,已废弃，因为使用命令行关闭程序会导致程序不能回写失败日志
     * @param ppid 启动火狐进程的pid
     * @throws IOException 异常
     */
    @Deprecated
    public static void killFirefoxAndItSelf(String ppid) throws IOException {
        if (OSUtils.isWindows()) {
            killFirefoxInWindows(ppid);
            exec(String.format("taskkill /pid %s -t -f", ppid));
        } else if (OSUtils.isLinux()) {
            killFirefoxInLinux(ppid);
            exec("kill -9 " + ppid);
        }
    }

    /**
     * windows下关闭火狐进程
     * @param ppid 启动火狐进程的pid
     * @throws IOException 异常
     */
    private static void killFirefoxInWindows(String ppid) throws IOException {
        String geckodriver = "geckodriver.exe";
        String killCmd= "taskkill /pid %s -t -f";
        String firefox = "firefox.exe";
        String findGeckodriverCmd= "wmic process where \"parentprocessid=" + ppid + " and name='" +
                geckodriver + "'\" get processid,parentprocessid,executablepath";
        String findFirefoxCmd= "wmic process where \"parentprocessid=%s and name='" + firefox +
                "'\" get processid,parentprocessid,executablepath,name";
        List<String> result = CommandUtil.run(findGeckodriverCmd), firefoxResult;
        Pattern p = Pattern.compile("\\s+");
        String[] split;
        String gid = "";

        for (String line : result) {
            //是火狐驱动的进程则进入关闭火狐浏览器的步骤
            if (line.contains(geckodriver)) {
                Matcher matcher = p.matcher(line);
                line = matcher.replaceAll(" ");
                split = line.split(" ");
                try {
                    gid = split[split.length - 1];
                    //关闭火狐浏览器
                    firefoxResult = CommandUtil.exec(String.format(findFirefoxCmd, gid));
                    for (String l : firefoxResult) {
                        matcher = p.matcher(l);
                        line = matcher.replaceAll(" ");
                        split = line.split(" ");
                        exec(String.format(killCmd, split[split.length - 1]));
                    }
                    exec(String.format(killCmd, gid));
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     * linux下关闭火狐进程
     * @param ppid 启动火狐进程的pid
     * @throws IOException 异常
     */
    private static void killFirefoxInLinux(String ppid) throws IOException {
        List<String> geckoDriverCommand = new ArrayList<>();
        geckoDriverCommand.add("/bin/sh");
        geckoDriverCommand.add("-c");
        geckoDriverCommand.add("ps -ef | grep geckodriver");

        List<String> result = CommandUtil.run(geckoDriverCommand.toArray(new String[0])), firefoxResult;
        String[] findFirefoxCmd = {"/bin/sh", "-c", "ps -ef | grep firefox"};

        Pattern p = Pattern.compile("\\s+");
        String[] split;
        String gid = "", killCmd;

        for (String line : result) {
            Matcher matcher = p.matcher(line);
            line = matcher.replaceAll(" ");
            split = line.split(" ");
            //第三个为父pid
            if (split.length > 3) {
                try {
                    //符合则关闭
                    if (split[2].equalsIgnoreCase(ppid)) {
                        gid = split[1];
                        firefoxResult = CommandUtil.exec(findFirefoxCmd);
                        //关闭火狐
                        for (String l : firefoxResult) {
                            matcher = p.matcher(l);
                            line = matcher.replaceAll(" ");
                            split = line.split(" ");
                            if (split.length > 3) {
                                if (split[2].equalsIgnoreCase(gid)) {
                                    killCmd = "kill -9 " + split[1];
                                    exec(killCmd);
                                }
                            }
                        }
                        killCmd = "kill -9 " + gid;
                        exec(killCmd);
                    }
                } catch (Exception e) {
                }
            }
        }
    }

}