package com.aichebaba.rooftop.utils;

import com.google.common.collect.ImmutableMap;
import com.jfinal.kit.PathKit;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ExcelUtils {

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

    public static Workbook export(String template, Map<String, Object> root) throws IOException {
        File f = new File(ExcelUtils.class.getResource("/excel").getFile(), template);
        Workbook wb = new HSSFWorkbook(new FileInputStream(f));
        Sheet sheet = wb.getSheetAt(0);
        for (int i = 0; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null || row.getCell(0) == null) {
                continue;
            }
            if ("items.start".equals(row.getCell(0).getStringCellValue())) {
                Row tempRow = sheet.getRow(i + 1);
                Collection<Object> items = (Collection<Object>) root.get("items");
                int index = i;
                int position = 0;
                for (Object item : items) {
                    position++;
                    if (index <= i + 1) {
                        newRow(sheet, index, tempRow, ImmutableMap.of("item", item, "position", position));
                    } else {
                        shiftRow(sheet, index, index + 1, tempRow, ImmutableMap.of("item", item, "position", position));
                    }
                    index++;
                }
                i += items.size() - 1;
                clearItems(sheet, i + position);
            } else {
                newRow(sheet, row.getRowNum(), row, root);
            }
        }
        return wb;
    }

    private static void clearItems(Sheet sheet, int rowNum) {
        Row row = sheet.getRow(rowNum);
        if (row != null) {
            for (Cell c : row) {
                c.setCellValue("");
            }
        }
    }

    protected static void newRow(Sheet sheet, int rowNum, Row row, Map<String, Object> root) {
        Row newRow = sheet.createRow(rowNum);
        CellStyle rowStyle = row.getRowStyle();
        if (rowStyle != null) {
            newRow.setRowStyle(rowStyle);
        }
        for (Cell c : row) {
            Cell newCell = newRow.createCell(c.getColumnIndex());
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

    /**
     * hdy
     * Excel File Output Stream 2016-9-22
     * @param excel
     * @param userId
     * @param fileName
     */
    public static void excelFileOutputStream(HSSFWorkbook excel, int userId, String fileName) {
        try {
            String path = PathKit.getWebRootPath() + "/download/" + userId;
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
            FileOutputStream fout = new FileOutputStream(path + fileName);
            excel.write(fout);
            fout.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
