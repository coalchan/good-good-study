package com.luckypeng.study.poi;

import org.apache.poi.ooxml.util.SAXHelper;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@link ExampleEventUserModel} 的生产版本，方便使用
 * @author coalchan
 * @date 2020/02/02
 */
public abstract class AbstractXlsxEventReader extends DefaultHandler {
    /**
     * 当前 shell 名称
     */
    private String curSheetName;

    /**
     * 当前行号
     */
    private int curRow;

    /**
     * 当前单元格名称，如 A3
     */
    private String curCellName = "";

    /**
     * 列名，获取第一行为列名，如 1 -> 序号
     */
    private Map<String, String> columnNames;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 共享字符串表
     */
    private SharedStringsTable sst;

    private String lastContents;
    private boolean nextIsString;
    private XSSFReader xssfReader;
    private XMLReader xmlReader;
    private StylesTable stylesTable;

    private boolean isDate;
    private short curDateIndex;
    private String curDatePattern;

    public AbstractXlsxEventReader(String fileName) {
        this.fileName = fileName;
        try {
            OPCPackage pkg = OPCPackage.open(fileName);
            xssfReader = new XSSFReader(pkg);
            sst = xssfReader.getSharedStringsTable();
            stylesTable = xssfReader.getStylesTable();
            xmlReader = SAXHelper.newXMLReader();
            xmlReader.setContentHandler(this);
        } catch (Exception e) {
            throw new RuntimeException(fileName + " 解析异常:", e);
        }
    }

    protected final DataFormatter formatter = new DataFormatter();
    public String date2Str(String cellValue, int dateIndex, String datePattern) {
        return formatter.formatRawCellContents(Double.parseDouble(cellValue), dateIndex, datePattern);
    }

    /**
     * 处理单元格内容
     * @param sheetName
     * @param curRow
     * @param cellName
     * @param columnName
     * @param cellValue
     */
    public abstract void optRows(String sheetName, int curRow, String cellName, String columnName, String cellValue);

    /**
     * 依次处理每个 sheet 的每一行
     */
    public void process() {
        try {
            XSSFReader.SheetIterator sheets = (XSSFReader.SheetIterator) xssfReader.getSheetsData();
            while (sheets.hasNext()) {
                curRow = 0;
                columnNames = new HashMap<>();
                InputStream sheet = sheets.next();
                curSheetName = sheets.getSheetName();
                InputSource sheetSource = new InputSource(sheet);
                xmlReader.parse(sheetSource);
                sheet.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(fileName + " 读取异常:", e);
        }
    }

    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes) {
        // c => cell
        if(name.equals("c")) {
            String cellName = attributes.getValue("r");
            if (cellName != null && !cellName.isEmpty()) {
                curCellName = cellName;
            }

            // 如果下一个元素是 SST 的索引，则将 nextIsString 标记为 true
            String cellType = attributes.getValue("t");
            if (cellType != null && cellType.equals("s")) {
                nextIsString = true;
            } else {
                nextIsString = false;
            }

            String cellStyleStr = attributes.getValue("s");
            if (cellStyleStr != null) {
                isDate = true;
                resolveDatePattern(cellStyleStr);
            } else {
                isDate = false;
            }
        }
        // Clear contents cache
        lastContents = "";
    }

    /**
     * 解析日期样式
     * @param cellStyleStr
     */
    private void resolveDatePattern(String cellStyleStr) {
        int styleIndex = Integer.parseInt(cellStyleStr);
        XSSFCellStyle style = stylesTable.getStyleAt(styleIndex);
        curDateIndex = style.getDataFormat();
        curDatePattern = style.getDataFormatString();
        if (curDatePattern == null) {
            curDatePattern = BuiltinFormats.getBuiltinFormat(curDateIndex);
        }
    }

    @Override
    public void endElement(String uri, String localName, String name) {
        // Process the last contents as required.
        // Do now, as characters() may be called more than once
        if(nextIsString) {
            int idx = Integer.parseInt(lastContents);
            lastContents = sst.getItemAt(idx).getString();
            nextIsString = false;
        }
        // v => contents of a cell
        // Output after we've seen the string contents
        if(name.equals("v")) {
            String cellValue = lastContents;
            if (curRow == 0) {
                columnNames.put(getColumnIndex(), cellValue);
            }
            if (isDate) {
                cellValue = date2Str(cellValue, curDateIndex, curDatePattern);
            }
            optRows(curSheetName, curRow, curCellName, columnNames.get(getColumnIndex()), cellValue);
        } else if (name.equals("row")) {
            // 说明已到行尾
            curRow++;
        }
    }

    private static final Pattern pattern = Pattern.compile("^([A-Z]+)\\d+");
    private String getColumnIndex() {
        Matcher m = pattern.matcher(curCellName);
        if (m.find()) {
            return m.group(1);
        }
        throw new RuntimeException("解析列出错: " + curCellName);
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        // 得到单元格内容的值
        lastContents += new String(ch, start, length);
    }
}
