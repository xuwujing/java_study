package com.pancm.excel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.util.CollectionUtils;
import com.alibaba.excel.write.merge.AbstractMergeStrategy;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.WriteTable;
import com.google.common.collect.Lists;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author pancm
 * @Title: pancm_project
 * @Description: 多行合并
 * 合并单元格
 * @Version:1.0.0
 * @Since:jdk1.8
 * @date 2023/3/23
 */
public class EasyExcelMergeTest {

    public static void main(String[] args) {
        writeExcel();
        writeExcel01();
        writeExcel02();
        writeExcel03();
    }


    private static String getPath(String s) {
        return System.getProperty("user.dir") + "/" + s+"_"+System.currentTimeMillis() + ".xlsx";
    }

    private static List<DemoData> data1() {
        List<DemoData> list = Lists.newArrayList();
        for (int i = 0; i < 3; i++) {
            DemoData data = new DemoData();
            data.setString("字符串" + 1);
            data.setDate(new Date());
            data.setDoubleData(0.56);
            list.add(data);
        }
        for (int i = 0; i < 3; i++) {
            DemoData data = new DemoData();
            data.setString("字符串" + 2);
            data.setDate(new Date());
            data.setDoubleData(0.56);
            list.add(data);
        }
        for (int i = 0; i < 4; i++) {
            DemoData data = new DemoData();
            data.setString("字符串" + 3);
            data.setDate(new Date());
            data.setDoubleData(0.57);
            list.add(data);
        }
        return list;
    }



    // 自定义合并策略 该类继承了AbstractMergeStrategy抽象合并策略，需要重写merge()方法
    public static class CustomMergeStrategy extends AbstractMergeStrategy {

        /**
         * 分组，每几行合并一次
         */
        private List<Integer> exportFieldGroupCountList;

        /**
         * 目标合并列index
         */
        private Integer targetColumnIndex;

        // 需要开始合并单元格的首行index
        private Integer rowIndex;

        // exportDataList为待合并目标列的值
        public CustomMergeStrategy(List<String> exportDataList, Integer targetColumnIndex) {
            this.exportFieldGroupCountList = getGroupCountList(exportDataList);
            this.targetColumnIndex = targetColumnIndex;
        }


        @Override
        protected void merge(Sheet sheet, Cell cell, Head head, Integer relativeRowIndex) {

            if (null == rowIndex) {
                rowIndex = cell.getRowIndex();
            }
            // 仅从首行以及目标列的单元格开始合并，忽略其他
            if (cell.getRowIndex() == rowIndex && cell.getColumnIndex() == targetColumnIndex) {
                mergeGroupColumn(sheet);
            }
        }

        private void mergeGroupColumn(Sheet sheet) {
            int rowCount = rowIndex;
            for (Integer count : exportFieldGroupCountList) {
                if (count == 1) {
                    rowCount += count;
                    continue;
                }
                // 合并单元格
                CellRangeAddress cellRangeAddress = new CellRangeAddress(rowCount, rowCount + count - 1, targetColumnIndex, targetColumnIndex);
                sheet.addMergedRegionUnsafe(cellRangeAddress);
                rowCount += count;
            }
        }

        // 该方法将目标列根据值是否相同连续可合并，存储可合并的行数
        private List<Integer> getGroupCountList(List<String> exportDataList) {
            if (CollectionUtils.isEmpty(exportDataList)) {
                return new ArrayList<>();
            }
            List<Integer> groupCountList = new ArrayList<>();
            int count = 1;
            for (int i = 1; i < exportDataList.size(); i++) {
                if (exportDataList.get(i).equals(exportDataList.get(i - 1))) {
                    count++;
                } else {
                    groupCountList.add(count);
                    count = 1;
                }
            }
            // 处理完最后一条后
            groupCountList.add(count);
            return groupCountList;
        }
    }

    // 单列多行合并
    public static void writeExcel() {
        String fileName = getPath("单列多行");
        ExcelWriter excelWriter = EasyExcel.write(fileName).excelType(ExcelTypeEnum.XLSX).build();

        List<DemoData> demoDataList = data1();
        // 写sheet的时候注册相应的自定义合并单元格策略
        WriteSheet writeSheet = EasyExcel.writerSheet("模板1").head(DemoData.class)
                .registerWriteHandler(new CustomMergeStrategy(demoDataList.stream().map(DemoData::getString).collect(Collectors.toList()), 0))
                .build();
        excelWriter.write(demoDataList, writeSheet);
        excelWriter.finish();
    }

    //多列多行合并
    public static void writeExcel01() {
        String fileName = getPath("多行多列");
        ExcelWriter excelWriter = EasyExcel.write(fileName).excelType(ExcelTypeEnum.XLSX).build();

        List<DemoData> demoDataList = data1();
        WriteSheet writeSheet = EasyExcel.writerSheet("模板1").head(DemoData.class)
                .registerWriteHandler(new CustomMergeStrategy(demoDataList.stream().map(DemoData::getString).collect(Collectors.toList()), 0))
                .registerWriteHandler(new CustomMergeStrategy(demoDataList.stream().map(o -> o.getDoubleData().toString()).collect(Collectors.toList()), 2))
                .build();
        excelWriter.write(demoDataList, writeSheet);
        excelWriter.finish();
    }
    //多sheet
    public static void writeExcel02() {
        String fileName = getPath("多sheet");
        ExcelWriter excelWriter = EasyExcel.write(fileName).excelType(ExcelTypeEnum.XLSX).build();

        List<DemoData> demoDataList = data1();
        WriteSheet writeSheet = EasyExcel.writerSheet("模板1").head(DemoData.class)
                .registerWriteHandler(new CustomMergeStrategy(demoDataList.stream().map(DemoData::getString).collect(Collectors.toList()), 0))
                .registerWriteHandler(new CustomMergeStrategy(demoDataList.stream().map(o -> o.getDoubleData().toString()).collect(Collectors.toList()), 2))
                .build();
        excelWriter.write(demoDataList, writeSheet);

        WriteSheet writeSheet1 = EasyExcel.writerSheet("模板2").head(DemoData.class).build();
        excelWriter.write(data1(), writeSheet1);
        excelWriter.finish();
    }

    //多表
    public static void writeExcel03() {
        String fileName = getPath("多表");
        ExcelWriter excelWriter = EasyExcel.write(fileName).excelType(ExcelTypeEnum.XLSX).build();
        WriteSheet writeSheet = EasyExcel.writerSheet("模板").needHead(Boolean.FALSE).build();

        List<DemoData> demoDataList = data1();
        // 需要表头设置为true，WriteTable一些属性会继承自WriteSheet
        WriteTable writeTable = EasyExcel.writerTable(1).head(DemoData.class).needHead(Boolean.TRUE)
                .registerWriteHandler(new CustomMergeStrategy(demoDataList.stream().map(DemoData::getString).collect(Collectors.toList()), 0))
                .registerWriteHandler(new CustomMergeStrategy(demoDataList.stream().map(o -> o.getDoubleData().toString()).collect(Collectors.toList()), 2))
                .build();
        excelWriter.write(demoDataList, writeSheet, writeTable);

        WriteTable writeTable1 = EasyExcel.writerTable(2).head(DemoData.class).needHead(Boolean.TRUE).build();
        excelWriter.write(data1(), writeSheet, writeTable1);
        excelWriter.finish();
    }

}

