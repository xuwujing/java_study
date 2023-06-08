package com.pancm.excel;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author pancm
 * @Title: pancm_project
 * @Description:
 * @Version:1.0.0
 * @Since:jdk1.8
 * @date 2023/3/23
 */
@Data
@EqualsAndHashCode
public class DemoData {
    private String string;
    private Date date;
    private Double doubleData;
}
