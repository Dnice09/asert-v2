package tz.go.mnrt.asert.modules.core.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReport;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tz.go.mnrt.asert.constants.Constant;
import tz.go.mnrt.asert.modules.core.services.ReportExportService;
import tz.go.mnrt.asert.modules.user.dtos.LoggedInUserDto;
import tz.go.mnrt.asert.configs.NoAuthorization;
import tz.go.mnrt.asert.modules.user.service.UserService;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import javax.validation.ValidationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(Constant.API_V1 + "/reports")
@RequiredArgsConstructor
public class ReportResource {
  private final ReportExportService reportExportService;
  private final UserService userService;

  @NoAuthorization
  @Transactional
  @GetMapping("/print")
  public ResponseEntity<?> print(
      HttpServletResponse response,
      @RequestParam("type") String type,
      @RequestParam(name = "format", defaultValue = "pdf") String format,
      @RequestParam(name = "adminHierarchyId", required = false) Long adminHierarchyId,
      @RequestParam(name = "startDate", required = false) String startDate,
      @RequestParam(name = "endDate", required = false) String endDate)
      throws JRException, SQLException, IOException {

    LoggedInUserDto user =
        userService
            .loggedIn()
            .orElseThrow(
                () ->
                    new ValidationException("Current user is not a dealer or doesnt have premise"));

    log.info("Finding current financial year");

    JasperReport jasperReport = reportExportService.getTemplate(type);

    /** Set report parameters */
    Map<String, Object> parameters = new HashMap<>();
    parameters.put(
        "admin_hierarchy_id",
        adminHierarchyId != null ? adminHierarchyId : user.getAdminHierarchyId());
    parameters.put("start_date", startDate);
    parameters.put("end_date", endDate != null ? endDate : LocalDate.now());

    InputStream logo = reportExportService.getLogo();

    if (logo != null) {
      parameters.put("logo", logo);
    }
    // Create Temporary file for report
    File tmpFile = null;
    switch (format) {
      case "pdf":
        // parameters.put()

        tmpFile =
            reportExportService.setParams(parameters).setJasperReport(jasperReport).exportToPdf();
        response.setContentType("application/pdf");
        //  response.addHeader("Content-Disposition", "attachment; filename=users.pdf");
        break;
      case "xls":
        tmpFile =
            reportExportService.setParams(parameters).setJasperReport(jasperReport).exportXls();
        response.setContentType("application/vnd.ms-excel");
        response.addHeader("Content-Disposition", "attachment; filename=users.xls");
        break;
      case "doc":
        tmpFile =
            reportExportService.setParams(parameters).setJasperReport(jasperReport).exportDoc();
        response.setContentType("application/msword");
        response.addHeader("Content-Disposition", "attachment; filename=users.doc");
        break;
    }

    Files.copy(tmpFile.toPath(), response.getOutputStream());
    response.getOutputStream().flush();
    tmpFile.delete();
    return null;
  }
}
