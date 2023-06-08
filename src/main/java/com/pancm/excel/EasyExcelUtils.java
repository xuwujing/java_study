package com.pancm.excel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EasyExcelUtils {

    public static void main(String[] args) {
        String[] headMap = { "项目名称", "楼栋名称", "单元名称", "楼层名称", "房间名称", "业主/租户姓名", "房间状态", "房间功能","认证人数","测试" };
        String[] dataStrMap={"hName","bName","uName","fName","pName","cName","pState","pFunction","pNum"};
        NoModelWriteData d = new NoModelWriteData();
        d.setFileName("认证统计");
        d.setHeadMap(headMap);
        d.setDataStrMap(dataStrMap);
        List<JSONObject> listDatas = new ArrayList<>();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("hName","项目1");
        jsonObject.put("bName","二楼");
        jsonObject.put("aa","测试");
        d.setDataList(listDatas);
        EasyExcelUtils easyExcelUtils = new EasyExcelUtils();
//        easyExcelUtils.jsonWrite(d, response);
    }

    //不创建对象的导出
    public void jsonWrite(NoModelWriteData data, HttpServletResponse response) throws IOException {
        // 这里注意 有同学反应使用swagger 会导致各种问题，请直接用浏览器或者用postman
        try {
//            response.setContentType("application/vnd.ms-excel");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileName = URLEncoder.encode(data.getFileName(), "UTF-8");
            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
            // 这里需要设置不关闭流
            EasyExcel.write(response.getOutputStream()).head(head(data.getHeadMap())).sheet(data.getFileName()).doWrite(dataList(data.getDataList(), data.getDataStrMap()));
        } catch (Exception e) {
            // 重置response
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            Map<String, String> map = new HashMap<String, String>();
            map.put("status", "failure");
            map.put("message", "下载文件失败" + e.getMessage());
            response.getWriter().println(JSON.toJSONString(map));
        }
    }

    //创建对象的导出
    public <T> void simpleWrite(SimpleWriteData data,Class<T> clazz, HttpServletResponse response) throws IOException {
        // 这里注意 有同学反应使用swagger 会导致各种问题，请直接用浏览器或者用postman
//        response.setContentType("application/vnd.ms-excel");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = URLEncoder.encode(data.getFileName(), "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), clazz).sheet(data.getFileName()).doWrite(data.getDataList());
    }

    //设置表头
    private List<List<String>> head(String[] headMap) {
        List<List<String>> list = new ArrayList<List<String>>();

        for (String head : headMap) {
            List<String> headList = new ArrayList<String>();
            headList.add(head);
            list.add(headList);
        }
        return list;
    }

    //设置导出的数据内容
    private List<List<Object>> dataList(List<JSONObject> dataList, String[] dataStrMap) {
        List<List<Object>> list = new ArrayList<List<Object>>();
        for (JSONObject map : dataList) {
            List<Object> data = new ArrayList<Object>();
            for (int i = 0; i < dataStrMap.length; i++) {
                data.add(map.get(dataStrMap[i]));
            }
            list.add(data);
        }
        return list;
    }
}

@Data
class NoModelWriteData implements Serializable {
    private String fileName;//文件名
    private String[] headMap;//表头数组
    private String[] dataStrMap;//对应数据字段数组
    private List<JSONObject> dataList;//数据集合
}

@Data
class SimpleWriteData implements Serializable {
    private String fileName;//文件名
    private List<?> dataList;//数据列表
}
