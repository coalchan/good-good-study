package com.luckypeng.study.poi.easyexcel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author coalchan
 * @date 2020/02/03
 */
@Slf4j
public class DataListener extends AnalysisEventListener<Map<Integer, String>> {
    @Getter
    private Map<String, List<Map<String, String>>> result = new HashMap<>();

    private long start = System.currentTimeMillis();
    private int size = 0;
    private Map<Integer, String> headMap;

    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        this.headMap = headMap;
    }

    @Override
    public void invoke(Map<Integer, String> data, AnalysisContext context) {
        log.info("解析到一条数据:{}", JSON.toJSONString(data));
        String sheetName = context.readSheetHolder().getSheetName();

        if (!result.containsKey(sheetName)) {
            result.put(sheetName, new ArrayList<>());
        }
        Map<String, String> row = new HashMap<>();

        headMap.entrySet().stream()
                .forEach(entry -> {
                    String value = data.get(entry.getKey());
                    row.put(entry.getValue(), value);
                });

        result.get(sheetName).add(row);

        size++;
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        long end = System.currentTimeMillis();
        long seconds = (end - start) / 1000;
        System.out.println("cost: " + seconds + "ms, size: " + size);
    }
}
