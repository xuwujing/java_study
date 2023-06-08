package com.pancm.ffmpeg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author pancm
 * @Title: gb28181_platform
 * @Description: FFmpeg相关的工具类
 * @Version:1.0.0
 * @Since:jdk1.8
 * @date 2021/4/15
 */
public class FFmpegUtil {


    /**
     *  执行ffmpeg的项目命令
     * @param cmdList
     * @throws IOException
     */
    public static void exec(List<String> cmdList) throws IOException {
        BufferedReader br = null;
        try {
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(cmdList);
            //正常信息和错误信息合并输出
            builder.redirectErrorStream(true);
            //开始执行命令
            Process process = builder.start();
            StringBuffer sbf = new StringBuffer();
            String line = null;
            br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((line = br.readLine()) != null) {
                sbf.append(line);
                sbf.append(" ");
            }
            String resultInfo = sbf.toString();
            System.out.println(resultInfo);
        } finally {
            if (br != null) {
                br.close();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        String ffmpegExePath = "C:\\ffmpeg\\bin\\ffmpeg.exe";
        String inputFilePath = "D:\\video\\ts\\25-16_940.ts";
        String outputFilePath = "D:\\video\\ts\\t3.jpg";
        List<String> command = new ArrayList<String>();
        command.add(ffmpegExePath);
        command.add("-i");
        command.add(inputFilePath);
        command.add("-f");
        command.add("image2");
        command.add("-ss");
        command.add("1");
        command.add("-t");
        command.add("0.001");
        command.add("-s");
        command.add("640*480");
        command.add(outputFilePath);
        exec(command);
    }
}
