package com.demo.utils;


/**
 * @author 李学照
 * @version 1.0
 * @date 2019/12/4
 * @desc
 */
public class HttpProxy {
    private String proxyIp;
    private int proxyPort;
    private String userName;
    private String passWord;

    public boolean isAuthenticate() {
        return true;
    }

    public String getProxyIp() {
        return proxyIp;
    }

    public void setProxyIp(String proxyIp) {
        this.proxyIp = proxyIp;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }
}
