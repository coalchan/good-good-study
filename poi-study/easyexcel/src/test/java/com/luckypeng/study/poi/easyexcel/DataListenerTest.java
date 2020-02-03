package com.luckypeng.study.poi.easyexcel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @see <a href="https://alibaba-easyexcel.github.io/quickstart/read.html"></a>
 */
public class DataListenerTest {
    @Test
    public void test() {
        String fileName = "../班级表.xlsx";

        DataListener listener = new DataListener();
        ExcelReader reader = EasyExcel.read(fileName, listener)
                .registerConverter(new CustomNumberConverter())
                .build();
        reader.readAll();

        // 这里千万别忘记关闭，读的时候会创建临时文件，到时磁盘会崩的
        reader.finish();

        Map<String, List<Map<String, String>>> data = listener.getResult();

        assertEquals(2, data.size());
        assertEquals(4, data.get("班级成员表").size());
        assertEquals(2, data.get("班级表").size());

        assertEquals("王五", data.get("班级成员表").get(3).get("姓名"));
        assertEquals("赵六", data.get("班级表").get(1).get("班主任"));

        assertEquals("1992-12-01", data.get("班级成员表").get(3).get("生日"));

        System.out.println(data.toString());
    }
}