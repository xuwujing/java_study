package com.pancm.ftp;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author pancm
 * @Title: leakproof-server
 * @Description: ftp帮助类
 * @Version:1.0.0
 * @Since:jdk1.8
 * @date 2021/8/18
 */
public class FtpHelper {

    /**
     * 获取文件列表文件的属性
     *
     * @param ip
     * @param port
     * @param user
     * @param pwd
     * @param url
     * @return
     */
    public static List<Map<String, String>> getListFiles(String ip, int port, String user, String pwd, String url) throws IOException {
        List<Map<String, String>> mapList = new ArrayList<>();
        FTPClient ftpClient = new FTPClient();
        ftpClient.connect(ip, port);
        ftpClient.login(user, pwd);
        FTPFile[] ftpFiles = ftpClient.listFiles(url);
        if(ftpFiles!=null && ftpFiles.length>0) {
            for (FTPFile ftpFile : ftpFiles) {
                Map<String, String> map = new HashMap<>();
                map.put("fileName",ftpFile.getName());
                map.put("fileSize",getSize(ftpFile.getSize()));
                map.put("fileTime",ftpFile.getTimestamp().getTime().toString());
                mapList.add(map);
            }
        }
        return mapList;
    }



    private static boolean testFtp(String ip, int port, String user, String pwd) throws IOException {
        FTPClient ftpClient = new FTPClient();
        ftpClient.connect(ip, port);//连接ftp
        ftpClient.login(user, pwd);//登陆ftp
        return FTPReply.isPositiveCompletion(ftpClient.getReplyCode());
    }




    public static String getSize(long size) {
        //获取到的size为：1705230
        long GB = 1024 * 1024 * 1024;//定义GB的计算常量
        long MB = 1024 * 1024;//定义MB的计算常量
        long KB = 1024;//定义KB的计算常量
        DecimalFormat df = new DecimalFormat("0.00");//格式化小数
        String resultSize = "";
        if (size / GB >= 1) {
            //如果当前Byte的值大于等于1GB
            resultSize = df.format(size / (float) GB) + "GB";
        } else if (size / MB >= 1) {
            //如果当前Byte的值大于等于1MB
            resultSize = df.format(size / (float) MB) + "MB";
        } else if (size / KB >= 1) {
            //如果当前Byte的值大于等于1KB
            resultSize = df.format(size / (float) KB) + "KB";
        } else {
            resultSize = size + "B";
        }
        return resultSize;
    }


    public static void main(String[] args) throws Exception {
        String ip = "192.168.10.90";
        int port = 21;
        String user = "root";
        String pwd = "lgwy@2020";
        String url = "/home/userfile/admin";
        System.out.println(testFtp(ip,port,user,pwd));
        System.out.println(getListFiles(ip,port,user,pwd,url));
    }
}
