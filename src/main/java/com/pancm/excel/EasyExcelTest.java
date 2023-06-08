package com.pancm.excel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.CellExtra;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author pancm
 * @Title: pancm_project
 * @Description: 参考: https://www.yuque.com/easyexcel/doc
 * @Version:1.0.0
 * @Since:jdk1.8
 * @date 2021/1/26
 */
@Slf4j
public class EasyExcelTest {


    /**
     * 最简单的读
     * <p>
     * 1. 创建excel对应的实体对象 参照{@link DemoData}
     * <p>
     * 2. 由于默认一行行的读取excel，所以需要创建excel一行一行的回调监听器，参照{@link DemoDataListener}
     * <p>
     * 3. 直接读即可
     */
    @Test
    public void simpleRead() {
        // 写法1：JDK8+ ,不用额外写一个DemoDataListener
        // since: 3.0.0-beta1
        String fileName = "/home" + "demo" + File.separator + "demo.xlsx";
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
        // 这里每次会读取3000条数据 然后返回过来 直接调用使用数据就行
//        EasyExcel.read(fileName, DemoData.class, new PageReadListener<DemoData>(dataList -> {
//            for (DemoData demoData : dataList) {
//                log.info("读取到一条数据{}", JSON.toJSONString(demoData));
//            }
//        })).sheet().doRead();

        // 写法2：
        // 匿名内部类 不用额外写一个DemoDataListener
        fileName = "/home" + "demo" + File.separator + "demo.xlsx";
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
        EasyExcel.read(fileName, DemoData.class, new ReadListener<DemoData>() {
            /**
             * 单次缓存的数据量
             */
            public static final int BATCH_COUNT = 100;
            /**
             *临时存储
             */
            private List<DemoData> cachedDataList = new ArrayList<>();

            /**
             * All listeners receive this method when any one Listener does an error report. If an exception is thrown here, the
             * entire read will terminate.
             *
             * @param exception
             * @param context
             * @throws Exception
             */
            @Override
            public void onException(Exception exception, AnalysisContext context) throws Exception {

            }

            /**
             * When analysis one head row trigger invoke function.
             *
             * @param headMap
             * @param context
             */
            @Override
            public void invokeHead(Map<Integer, CellData> headMap, AnalysisContext context) {

            }

            @Override
            public void invoke(DemoData data, AnalysisContext context) {
                cachedDataList.add(data);
                if (cachedDataList.size() >= BATCH_COUNT) {
                    saveData();
                    // 存储完成清理 list
                    cachedDataList = new ArrayList<>();
                }
            }

            /**
             * The current method is called when extra information is returned
             *
             * @param extra   extra information
             * @param context
             */
            @Override
            public void extra(CellExtra extra, AnalysisContext context) {

            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {
                saveData();
            }

            /**
             * Verify that there is another piece of data.You can stop the read by returning false
             *
             * @param context
             * @return
             */
            @Override
            public boolean hasNext(AnalysisContext context) {
                return false;
            }

            /**
             * 加上存储数据库
             */
            private void saveData() {
                log.info("{}条数据，开始存储数据库！", cachedDataList.size());
                log.info("存储数据库成功！");
            }
        }).sheet().doRead();

        // 有个很重要的点 DemoDataListener 不能被spring管理，要每次读取excel都要new,然后里面用到spring可以构造方法传进去
        // 写法3：
        fileName = "/home" + "demo" + File.separator + "demo.xlsx";
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
        EasyExcel.read(fileName, DemoData.class, new DemoDataListener()).sheet().doRead();

        // 写法4：
        fileName = "/home" + "demo" + File.separator + "demo.xlsx";
        // 一个文件一个reader
        ExcelReader excelReader = null;
        try {
            excelReader = EasyExcel.read(fileName, DemoData.class, new DemoDataListener()).build();
            // 构建一个sheet 这里可以指定名字或者no
            ReadSheet readSheet = EasyExcel.readSheet(0).build();
            // 读取一个sheet
            excelReader.read(readSheet);
        } finally {
            if (excelReader != null) {
                // 这里千万别忘记关闭，读的时候会创建临时文件，到时磁盘会崩的
                excelReader.finish();
            }
        }
    }


    @Data
    @EqualsAndHashCode
    class DemoData {
        private String string;
        private Date date;
        private Double doubleData;
    }


    class DemoDataListener implements ReadListener<DemoData> {

        /**
         * 每隔5条存储数据库，实际使用中可以100条，然后清理list ，方便内存回收
         */
        private static final int BATCH_COUNT = 100;
        /**
         * 缓存的数据
         */
        private List<DemoData> cachedDataList = new ArrayList<>();
        /**
         * 假设这个是一个DAO，当然有业务逻辑这个也可以是一个service。当然如果不用存储这个对象没用。
         */
//    private DemoDAO demoDAO;
//
//    public DemoDataListener() {
//        // 这里是demo，所以随便new一个。实际使用如果到了spring,请使用下面的有参构造函数
//        demoDAO = new DemoDAO();
//    }

        /**
         * 如果使用了spring,请使用这个构造方法。每次创建Listener的时候需要把spring管理的类传进来
         *
         * @param demoDAO
         */
//    public DemoDataListener(DemoDAO demoDAO) {
//        this.demoDAO = demoDAO;
//    }

        /**
         * All listeners receive this method when any one Listener does an error report. If an exception is thrown here, the
         * entire read will terminate.
         *
         * @param exception
         * @param context
         * @throws Exception
         */
        @Override
        public void onException(Exception exception, AnalysisContext context) throws Exception {

        }

        /**
         * When analysis one head row trigger invoke function.
         *
         * @param headMap
         * @param context
         */
        @Override
        public void invokeHead(Map<Integer, CellData> headMap, AnalysisContext context) {

        }

        /**
         * 这个每一条数据解析都会来调用
         *
         * @param data    one row value. Is is same as {@link AnalysisContext#readRowHolder()}
         * @param context
         */
        @Override
        public void invoke(DemoData data, AnalysisContext context) {
            log.info("解析到一条数据:{}", JSON.toJSONString(data));
            cachedDataList.add(data);
            // 达到BATCH_COUNT了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
            if (cachedDataList.size() >= BATCH_COUNT) {
                saveData();
                // 存储完成清理 list
//            cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
            }
        }

        /**
         * The current method is called when extra information is returned
         *
         * @param extra   extra information
         * @param context
         */
        @Override
        public void extra(CellExtra extra, AnalysisContext context) {

        }

        /**
         * 所有数据解析完成了 都会来调用
         *
         * @param context
         */
        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
            // 这里也要保存数据，确保最后遗留的数据也存储到数据库
            saveData();
            log.info("所有数据解析完成！");
        }

        /**
         * Verify that there is another piece of data.You can stop the read by returning false
         *
         * @param context
         * @return
         */
        @Override
        public boolean hasNext(AnalysisContext context) {
            return false;
        }

        /**
         * 加上存储数据库
         */
        private void saveData() {
            log.info("{}条数据，开始存储数据库！", cachedDataList.size());
//        demoDAO.save(cachedDataList);
            log.info("存储数据库成功！");
        }
    }
}
