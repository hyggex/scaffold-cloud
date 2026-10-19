package com.scaffold.common.core.utils.excel;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;

/**
 * Apache POI Excel 导出工具类。
 *
 * <p>支持 Excel 2007+（.xlsx）格式，使用 SXSSFWorkbook 流式写入，大数据量不内存溢出。</p>
 *
 * <p>使用示例：</p>
 * <pre>{@code
 * List<ExcelColumn> columns = List.of(
 *     new ExcelColumn("userName", "用户名"),
 *     new ExcelColumn("nickName", "昵称")
 * );
 * ExcelUtils.exportExcel(userList, columns, response, "用户信息", "用户Sheet");
 * }</pre>
 *
 *  @author 8800
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExcelUtils {

    /** Excel 列宽默认值（单位：字符） */
    private static final int DEFAULT_COLUMN_WIDTH = 15;

    /** 大数据量阈值（超过此行数自动使用流式写入） */
    private static final int FLUSH_ROWS = 1000;

    /**
     * 通用导出：自定义列映射导出到 Excel。
     *
     * @param data       数据列表
     * @param columns    列配置列表（field=字段名，title=表头标题）
     * @param response  HTTP 响应
     * @param fileName  文件名（不含扩展名）
     * @param sheetName 工作表名
     * @param <T>       数据类型
     */
    public static <T> void exportExcel(Collection<T> data, List<ExcelColumn> columns,
                                       HttpServletResponse response,
                                       String fileName, String sheetName) {
        if (data == null || data.isEmpty()) {
            writeEmptyResponse(response, fileName);
            return;
        }

        SXSSFWorkbook workbook = new SXSSFWorkbook(100);
        Sheet sheet = workbook.createSheet(sheetName);

        try {
            // 1. 生成表头
            createHeaderRow(sheet, columns);

            // 2. 写入数���行，返回每列最大宽度
            int[] maxWidths = createDataRows(sheet, data, columns);

            // 3. 设置列宽
            for (int i = 0; i < maxWidths.length; i++) {
                sheet.setColumnWidth(i, maxWidths[i] * 256);
            }

            // 4. 写出到响应
            writeResponse(workbook, response, fileName);

        } catch (Exception e) {
            log.error("Excel 导出失败: {}", e.getMessage(), e);
            throw new RuntimeException("Excel 导出失败: " + e.getMessage(), e);
        } finally {
            workbook.dispose();
            try {
                workbook.close();
            } catch (IOException e) {
                log.warn("关闭 Excel workbook 失败: {}", e.getMessage());
            }
        }
    }

    /**
     * 生成表头行。
     */
    private static void createHeaderRow(Sheet sheet, List<ExcelColumn> columns) {
        Row headerRow = sheet.createRow(0);
        CellStyle headerStyle = createHeaderStyle(headerRow.getSheet().getWorkbook());

        for (int i = 0; i < columns.size(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns.get(i).getTitle());
            cell.setCellStyle(headerStyle);
        }
    }

    /**
     * 创建表头单元格样式（加粗、浅灰背景）。
     */
    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    /**
     * 写入数据行，返回每列最大宽度。
     */
    private static <T> int[] createDataRows(Sheet sheet, Collection<T> data,
                                            List<ExcelColumn> columns) {
        int[] maxWidths = new int[columns.size()];
        int rowIndex = 1;

        for (T item : data) {
            Row row = sheet.createRow(rowIndex++);

            for (int colIndex = 0; colIndex < columns.size(); colIndex++) {
                ExcelColumn col = columns.get(colIndex);
                Cell cell = row.createCell(colIndex);
                Object value = getFieldValue(item, col.getField());

                if (value != null) {
                    String str = value.toString();
                    cell.setCellValue(str);
                    int len = Math.min(str.length(), DEFAULT_COLUMN_WIDTH);
                    maxWidths[colIndex] = Math.max(maxWidths[colIndex], len);
                }
            }
        }
        return maxWidths;
    }

    /**
     * 通过反射获取对象字段值，支持嵌套字段（如 user.dept.name）。
     */
    private static <T> Object getFieldValue(T item, String field) {
        try {
            Object value = item;
            Class<?> clazz = item.getClass();

            for (String part : field.split("\\.")) {
                Field f = findField(clazz, part);
                if (f == null) return null;
                f.setAccessible(true);
                value = f.get(value);
                if (value == null) return null;
                clazz = value.getClass();
            }
            return value;
        } catch (Exception e) {
            log.warn("获取字段 {} 失败: {}", field, e.getMessage());
            return null;
        }
    }

    /**
     * 查找字段（支持父类）。
     */
    private static Field findField(Class<?> clazz, String name) {
        while (clazz != null) {
            try {
                return clazz.getDeclaredField(name);
            } catch (NoSuchFieldException ignored) {
                clazz = clazz.getSuperclass();
            }
        }
        return null;
    }

    /**
     * 写入空响应（无数据时）。
     */
    private static void writeEmptyResponse(HttpServletResponse response, String fileName) {
        try {
            setResponseHeader(response, fileName);
            response.getOutputStream().close();
        } catch (IOException ignored) {}
    }

    /**
     * 写出 Excel 到响应流。
     */
    private static void writeResponse(Workbook workbook, HttpServletResponse response,
                                      String fileName) throws IOException {
        setResponseHeader(response, fileName);
        try (OutputStream out = response.getOutputStream()) {
            workbook.write(out);
            out.flush();
        }
    }

    /**
     * 设置响应头。
     */
    private static void setResponseHeader(HttpServletResponse response, String fileName) {
        try {
            String encoded = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");
            response.setContentType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setHeader("Content-Disposition",
                "attachment;filename=" + encoded + ".xlsx");
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
        } catch (Exception e) {
            throw new RuntimeException("设置响应头失败: " + e.getMessage(), e);
        }
    }

    /**
     * Excel 列定义。
     *
     * @param field 字段名（支持嵌套，如 user.dept.name）
     * @param title 列标题（显示在表头）
     */
    @Data
    @NoArgsConstructor
    public static class ExcelColumn {
        /** 字段名（支持嵌套路径） */
        private String field;
        /** 列标题（显示在表头） */
        private String title;

        public ExcelColumn(String field, String title) {
            this.field = field;
            this.title = title;
        }
    }
}
