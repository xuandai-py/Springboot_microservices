package com.pinky.admin.user;

import com.pinky.common.entity.User;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AbstractExporter {

    public void setResponseHeader(HttpServletResponse httpServletResponse, String contentType, String extension) throws IOException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String timeStamp = dateFormat.format(new Date());
        String fileName = "users_" + timeStamp + extension;

        httpServletResponse.setContentType(contentType);
        String headerKey = "Content-Disposition";
        String headerValues = "attachment; filename=" + fileName;
        httpServletResponse.setHeader(headerKey, headerValues);
    }
}
