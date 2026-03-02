package tz.go.mnrt.asert.modules.core.services;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.util.JRSaver;
import net.sf.jasperreports.export.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class ReportExportService {

    private final DataSource dataSource;

    private Map<String, Object> params;

    private JasperReport jasperReport;

    @Autowired
    public ReportExportService(DataSource dataSource) {

        this.dataSource = dataSource;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public ReportExportService setParams(Map<String, Object> parameters) {
        this.params = parameters;
        return this;
    }

    public JasperReport getJasperReport() {
        return jasperReport;
    }

    public ReportExportService setJasperReport(JasperReport jasperReport) {
        this.jasperReport = jasperReport;
        return this;
    }

    public JasperReport getTemplate(String type) throws IOException, JRException {

        InputStream reportStream = getClass().getResourceAsStream("/jasper-templates/".concat(type).concat(".jrxml"));

        /** Compile/get compiles file */
        JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
        JRSaver.saveObject(jasperReport, Files.createTempFile(type, ".jasper").toFile());
        reportStream.close();
        return jasperReport;
    }

    public InputStream getLogo() {
        InputStream logo = getClass().getResourceAsStream("/jasper-templates/logo".concat(".png"));
        return logo;
    }

    public File exportToPdf(JRDataSource dataSource) throws SQLException, IOException, JRException {

        File tmpFile = Files.createTempFile("report", ".pdf").toFile();
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);

        JRPdfExporter exporter = new JRPdfExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(tmpFile));

        SimplePdfReportConfiguration reportConfig = new SimplePdfReportConfiguration();
        reportConfig.setSizePageToContent(true);
        reportConfig.setForceLineBreakPolicy(true);

        SimplePdfExporterConfiguration exportConfig = new SimplePdfExporterConfiguration();
        exportConfig.setAllowedPermissionsHint("PRINTING");

        exporter.setConfiguration(reportConfig);
        exporter.setConfiguration(exportConfig);

        exporter.exportReport();
        return tmpFile;
    }

    public File exportToPdf() throws SQLException, IOException, JRException {

        File tmpFile = Files.createTempFile(LocalDateTime.now().toString(), ".pdf").toFile();
        Connection connection = dataSource.getConnection();
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, connection);

        JRPdfExporter exporter = new JRPdfExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(tmpFile));

        SimplePdfReportConfiguration reportConfig = new SimplePdfReportConfiguration();
        reportConfig.setSizePageToContent(true);
        reportConfig.setForceLineBreakPolicy(true);

        SimplePdfExporterConfiguration exportConfig = new SimplePdfExporterConfiguration();
        exportConfig.setMetadataAuthor("ZANMUTM");

        exporter.setConfiguration(reportConfig);
        exporter.setConfiguration(exportConfig);

        exporter.exportReport();
        connection.close();
        return tmpFile;
    }

    public File exportXls(JRDataSource dataSource) throws IOException, JRException, SQLException {

        File tmpFile = Files.createTempFile(LocalDateTime.now().toString(), ".xls").toFile();

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);

        JRXlsxExporter exporter = new JRXlsxExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(tmpFile));

        SimpleXlsxReportConfiguration reportConfig = new SimpleXlsxReportConfiguration();
        reportConfig.setSheetNames(new String[]{"Payroll"});

        exporter.setConfiguration(reportConfig);
        exporter.exportReport();

        return tmpFile;
    }

    public File exportXls() throws IOException, JRException, SQLException {

        File tmpFile = Files.createTempFile(LocalDateTime.now().toString(), ".xls").toFile();

        Connection connection = dataSource.getConnection();

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, connection);

        JRXlsxExporter exporter = new JRXlsxExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(tmpFile));

        SimpleXlsxReportConfiguration reportConfig = new SimpleXlsxReportConfiguration();
        reportConfig.setSheetNames(new String[]{"Employee Data"});

        exporter.setConfiguration(reportConfig);
        exporter.exportReport();
        connection.close();

        return tmpFile;
    }

    public File exportDoc() throws IOException, JRException, SQLException {

        File tmpFile = Files.createTempFile(LocalDateTime.now().toString(), ".doc").toFile();

        Connection connection = dataSource.getConnection();

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, connection);

        JRDocxExporter exporter = new JRDocxExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(tmpFile));

        SimpleDocxExporterConfiguration reportConfig = new SimpleDocxExporterConfiguration();
        reportConfig.setMetadataTitle("ZAN-MUTM");

        exporter.setConfiguration(reportConfig);
        exporter.exportReport();
        connection.close();
        return tmpFile;
    }
}
