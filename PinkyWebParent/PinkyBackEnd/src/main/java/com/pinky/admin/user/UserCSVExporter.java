package com.pinky.admin.user;

import com.pinky.common.entity.User;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class UserCSVExporter extends AbstractExporter{

    public void export(List<User> listUser, HttpServletResponse httpServletResponse) throws IOException {
        super.setResponseHeader(httpServletResponse, "text/csv", ".csv");

        ICsvBeanWriter iCsvBeanWriter = new CsvBeanWriter(
                httpServletResponse.getWriter(),
                CsvPreference.EXCEL_PREFERENCE);

        String[] csvHeader = {"User ID", "E-mail", "First Name", "Last Name", "Roles", "Enabled"};
        String[] csvMapping = {"id", "email", "firstName", "lastName", "roles", "enabled"};

        iCsvBeanWriter.writeHeader(csvHeader);

        for(User user : listUser){
            iCsvBeanWriter.write(user, csvMapping);
        }
        iCsvBeanWriter.close();

    }
}
