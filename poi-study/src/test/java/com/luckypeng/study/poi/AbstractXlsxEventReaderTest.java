package com.luckypeng.study.poi;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class AbstractXlsxEventReaderTest {

    @Test
    public void process() {
        String fileName = "班级表.xlsx";
        Map<String, List<Map<String, String>>> data = new HashMap<>();
        AbstractXlsxEventReader reader = new AbstractXlsxEventReader(fileName) {
            @Override
            public String date2Str(String cellValue, int dateIndex, String datePattern) {
                if (datePattern.contains("yy") && !datePattern.contains("h")) {
                    datePattern = "yyyy-MM-dd";
                } else if (datePattern.contains("h") && !datePattern.contains("yy")){
                    datePattern = "hh:mm:ss";
                } else {
                    datePattern = "yyyy-MM-dd hh:mm:ss";
                }
                return formatter.formatRawCellContents(Double.parseDouble(cellValue), dateIndex, datePattern);
            }

            @Override
            public void optRows(String sheetName, int curRow, String cellName, String columnName, String cellValue) {
                if (curRow > 0) {
                    if (!data.containsKey(sheetName)) {
                        data.put(sheetName, new ArrayList<>());
                    }
                    if (data.get(sheetName).size() < curRow) {
                        Map<String, String> row = new HashMap<>();
                        data.get(sheetName).add(row);
                    }
                    data.get(sheetName).get(curRow - 1).put(columnName, cellValue.trim());
                }
            }
        };

        reader.process();

        assertEquals(2, data.size());
        assertEquals(3, data.get("班级成员表").size());
        assertEquals(2, data.get("班级表").size());

        assertEquals("王五", data.get("班级成员表").get(2).get("姓名"));
        assertEquals("赵六", data.get("班级表").get(1).get("班主任"));

        assertEquals("1992-12-01", data.get("班级成员表").get(2).get("生日"));

        System.out.println(data.toString());
    }
}