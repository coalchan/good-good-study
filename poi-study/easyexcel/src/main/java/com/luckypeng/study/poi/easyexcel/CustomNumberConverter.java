package com.luckypeng.study.poi.easyexcel;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;

/**
 * @author coalchan
 * @date 2020/02/03
 */
public class CustomNumberConverter implements Converter<String> {
    @Override
    public Class supportJavaTypeKey() {
        return String.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.NUMBER;
    }

    @Override
    public String convertToJavaData(CellData cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        String datePattern = cellData.getDataFormatString();
        if (datePattern == null && cellData.getDataFormat() != null) {
            datePattern = BuiltinFormats.getBuiltinFormat(cellData.getDataFormat());
        }

        // If there are "DateTimeFormat", read as date
        if (contentProperty != null && contentProperty.getDateTimeFormatProperty() != null) {
            return toDateStr(cellData, datePattern);
        }
        // If there are "NumberFormat", read as number
        if (contentProperty != null && contentProperty.getNumberFormatProperty() != null) {
            return cellData.getNumberValue().toPlainString();
        }
        // Excel defines formatting
        if (cellData.getDataFormat() != null) {
            if (DateUtil.isADateFormat(cellData.getDataFormat(), cellData.getDataFormatString())) {
                return toDateStr(cellData, datePattern);
            } else {
                return cellData.getNumberValue().toPlainString();
            }
        }
        // Default conversion number
        return cellData.getNumberValue().toPlainString();
    }

    private final DataFormatter formatter = new DataFormatter();
    private String toDateStr(CellData cellData, String datePattern) {
        if (datePattern.contains("yy") && datePattern.contains("h:")) {
            datePattern = "yyyy-MM-dd hh:mm:ss";
        } else if (datePattern.contains("h:") && !datePattern.contains("yy")){
            datePattern = "hh:mm:ss";
        } else {
            datePattern = "yyyy-MM-dd";
        }
        return formatter.formatRawCellContents(cellData.getNumberValue().doubleValue(), cellData.getDataFormat(), datePattern);
    }

    /**
     * 写的时候会用到
     * @param value
     * @param contentProperty
     * @param globalConfiguration
     * @return
     * @throws Exception
     */
    @Override
    public CellData convertToExcelData(String value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        return null;
    }
}
