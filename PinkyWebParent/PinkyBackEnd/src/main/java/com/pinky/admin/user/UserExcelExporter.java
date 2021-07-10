package com.pinky.admin.user;

import com.pinky.common.entity.User;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.*;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class UserExcelExporter extends AbstractExporter{

    private XSSFWorkbook xssfWorkbook = new XSSFWorkbook();
    private XSSFSheet xssfSheet;

//    public void UserExcelExporter(){
//        xssfWorkbook = new XSSFWorkbook();
//    }
    private void writeHeaderLine(){
        xssfSheet= xssfWorkbook.createSheet("Users");
        XSSFRow xssfRow = xssfSheet.createRow(0);

        XSSFCellStyle xssfCellStyle = xssfWorkbook.createCellStyle();
        XSSFFont xssfFont = xssfWorkbook.createFont();
        xssfFont.setBold(true);
        xssfFont.setFontHeight(16);
        xssfCellStyle.setFont(xssfFont);

        createCell(xssfRow, 0, "User Id", xssfCellStyle);
        createCell(xssfRow, 1, "Email", xssfCellStyle);
        createCell(xssfRow, 2, "First Name", xssfCellStyle);
        createCell(xssfRow, 3, "Last Name", xssfCellStyle);
        createCell(xssfRow, 4, "Roles", xssfCellStyle);
        createCell(xssfRow, 5, "Enabled", xssfCellStyle);
    }

    private void createCell(XSSFRow row, int columnIndex,
                            Object value, CellStyle style){

        XSSFCell xssfCell = row.createCell(columnIndex);
        xssfSheet.autoSizeColumn(columnIndex);
        if(value instanceof Integer){
            xssfCell.setCellValue((Integer) value);
        } else if(value instanceof Boolean){
            xssfCell.setCellValue((Boolean) value);
        } else {
            xssfCell.setCellValue((String) value);
        }

        xssfCell.setCellStyle(style);

    }

    public void export(List<User> listUser, HttpServletResponse httpServletResponse) throws IOException {
        super.setResponseHeader(httpServletResponse, "application/octet-stream", ".xlsx");

        writeHeaderLine();
        writeDataLine(listUser);

        ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();
        xssfWorkbook.write(servletOutputStream);
        xssfWorkbook.close();
        servletOutputStream.close();

    }

    public void writeDataLine(List<User> listUser){
        int rowIndex = 1;

        XSSFCellStyle xssfCellStyle = xssfWorkbook.createCellStyle();
        XSSFFont xssfFont = xssfWorkbook.createFont();
        xssfFont.setFontHeight(14);
        xssfCellStyle.setFont(xssfFont);

        for(User user : listUser){
            XSSFRow row = xssfSheet.createRow(rowIndex++);
            int columnIndex = 0;

            createCell(row, columnIndex++, user.getId(), xssfCellStyle);
            createCell(row, columnIndex++, user.getEmail(), xssfCellStyle);
            createCell(row, columnIndex++, user.getFirstName(), xssfCellStyle);
            createCell(row, columnIndex++, user.getLastName(), xssfCellStyle);
            createCell(row, columnIndex++, user.getRoles().toString(), xssfCellStyle);
            createCell(row, columnIndex++, user.isEnabled(), xssfCellStyle);
        }
    }

}
