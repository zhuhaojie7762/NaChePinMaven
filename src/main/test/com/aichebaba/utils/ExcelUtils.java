package com.aichebaba.utils;


import com.aichebaba.rooftop.utils.TemplateUtils;
import com.google.common.collect.ImmutableMap;
import junit.framework.TestCase;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ExcelUtils extends TestCase {

    public static void main(String[] args) throws IOException {
        File file = new File("E:/cities.xls");
        parseExcel(file, new Converter<String>() {
            @Override
            public String convert(Row row) {
                System.out.print("省-" + row.getCell(0).getStringCellValue()
                        + ", 市-" + row.getCell(1).getStringCellValue()
                        + ", 区-" + row.getCell(2).getStringCellValue());
                System.out.println();
                return row.getCell(0).getStringCellValue();
            }
        });
        System.out.println(file.exists());
    }

    public static <T> List<T> parseExcel(File excel, Converter<T> converter) throws IOException {
        Workbook wb = new HSSFWorkbook(new FileInputStream(excel));
        Sheet sheet = wb.getSheetAt(0);
        List<T> list = new ArrayList<>();
        for (int i = 0; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                break;
            }
            list.add(converter.convert(row));
        }
        return list;
    }

    public void testExcelExport() throws URISyntaxException, IOException {
        List<Map<String, Object>> data = new ArrayList<>();
        data.add(ImmutableMap.<String, Object>of("code", "1", "name", "name1"));
        data.add(ImmutableMap.<String, Object>of("code", "2", "name", "name2"));
        data.add(ImmutableMap.<String, Object>of("code", "3", "name", "name3"));
        File f = new File("E://test.xls");
        Workbook wb = export(f, ImmutableMap.<String, Object>of("items", data, "total", "total222"));
        File t = new File("E://t.xls");
        FileOutputStream out = new FileOutputStream(t);
        wb.write(out);
        out.flush();
        out.close();
    }

    public static Workbook export(File templateFile, Map<String, Object> root) throws IOException {
        Workbook wb = new HSSFWorkbook(new FileInputStream(templateFile));
        Sheet sheet = wb.getSheetAt(0);
        for (int i = 0; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row.getCell(0) == null) {
                continue;
            }
            if ("items.start".equals(row.getCell(0).getStringCellValue())) {
                Row tempRow = sheet.getRow(i + 1);
                Collection<Object> items = (Collection<Object>) root.get("items");
                int index = i;
                for (Object item : items) {
                    if (index <= i + 1) {
                        newRow(sheet, index, tempRow, ImmutableMap.of("item", item));
                    } else {
                        shiftRow(sheet, index, index + 1, tempRow, ImmutableMap.of("item", item));
                    }
                    index++;
                }
                i += items.size() - 1;
            } else {
                newRow(sheet, row.getRowNum(), row, root);
            }
        }
        return wb;
    }

    protected static void newRow(Sheet sheet, int rowNum, Row row, Map<String, Object> root) {
        Row newRow = sheet.createRow(rowNum);
        for (Cell c : row) {
            String value = c.getStringCellValue();
            if (value.contains("{")) {
                value = parse(value, root);
            }
            newRow.createCell(c.getColumnIndex()).setCellValue(value);
        }
    }

    protected static void shiftRow(Sheet newSheet, int from, int end, Row row, Map<String, Object> root) {
        newSheet.shiftRows(from, end, 1);
        Row newRow = newSheet.createRow(from);
        CellStyle rowStyle = row.getRowStyle();
        if (rowStyle != null) {
            newRow.setRowStyle(rowStyle);
        }
        int cellIndex = 0;
        for (Cell c : row) {
            Cell newCell = newRow.createCell(cellIndex++);
            CellStyle cellStyle = c.getCellStyle();
            if (cellStyle != null) {
                newCell.setCellStyle(cellStyle);
            }
            String value = c.getStringCellValue();
            if (value.contains("{")) {
                value = parse(value, root);
            }
            newCell.setCellValue(value);
        }
    }

    private static String parse(String expression, Map<String, Object> root) {
        return TemplateUtils.parseText(expression, root);
    }

    public interface Converter<T> {
        T convert(Row row);
    }
}
