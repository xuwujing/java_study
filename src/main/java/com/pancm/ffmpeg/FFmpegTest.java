package com.pancm.ffmpeg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author pancm
 * @Title: FFmpegTest
 * @Description: FFmpeg获取视频图片
 * 调用FFmpeg的命令获取ts视频的图片
 * @Version:1.0.0
 * @Since:jdk1.8
 * @Date 2021/4/14
 **/
public class FFmpegTest {


    public static void main(String[] args) {

        String ffmpegExePath = "C:\\ffmpeg\\bin\\ffmpeg.exe";

        String inputFilePath = "D:\\video\\ts\\25-16_940.ts";

        String outputFilePath = "D:\\video\\ts\\t2.jpg";

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
        command.add("320*240");
        command.add(outputFilePath);
        ProcessBuilder builder = new ProcessBuilder();
        builder.command(command);
        //正常信息和错误信息合并输出
        builder.redirectErrorStream(true);
        try {
            //开始执行命令
            Process process = builder.start();
            //如果你想获取到执行完后的信息，那么下面的代码也是需要的
            StringBuffer sbf = new StringBuffer();
            String line = null;
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((line = br.readLine()) != null) {
                sbf.append(line);
                sbf.append(" ");
            }
            String resultInfo = sbf.toString();
            System.out.println(resultInfo);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
