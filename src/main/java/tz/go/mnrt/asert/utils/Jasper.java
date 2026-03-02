package tz.go.mnrt.asert.utils;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;

@Service
public class Jasper {
    public void exportExcel(HttpServletResponse response, JasperPrint jasperPrint) throws JRException, IOException {
        JRXlsxExporter exporter = new JRXlsxExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        ByteArrayOutputStream reportStream = new ByteArrayOutputStream();
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(reportStream));
        SimpleXlsxReportConfiguration reportConfig = new SimpleXlsxReportConfiguration();
        reportConfig.setSheetNames(new String[]{"Data"});
        exporter.setConfiguration(reportConfig);
        exporter.exportReport();
        String fileName = LocalDateTime.now() + "-file.xlsx";
        response.setContentType("application/vnd.ms-excel");
        response.addHeader("Content-Disposition", "inline; filename=" + fileName + ";");

        OutputStream responseOutputStream = response.getOutputStream();
        responseOutputStream.write(reportStream.toByteArray());
        responseOutputStream.close();
        reportStream.close();
    }

    public void exportPdf(JasperPrint jasperPrint, HttpServletResponse response) throws JRException, IOException {
        JRPdfExporter pdfExporter = new JRPdfExporter();
        pdfExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        ByteArrayOutputStream pdfReportStream = new ByteArrayOutputStream();
        pdfExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(pdfReportStream));
        pdfExporter.exportReport();
        String receiptPdfFile = "export.pdf";
        response.setContentType("application/pdf");
        response.setHeader("Content-Length", String.valueOf(pdfReportStream.size()));
        response.addHeader("Content-Disposition", "inline; filename=" + receiptPdfFile + ";");

        OutputStream responseOutputStream = response.getOutputStream();
        responseOutputStream.write(pdfReportStream.toByteArray());
        responseOutputStream.close();
        pdfReportStream.close();
    }

    public String exportBase64(JasperPrint jasperPrint) throws JRException {
        byte[] pdf = JasperExportManager.exportReportToPdf(jasperPrint);
        return Util.convertFileToBase64(pdf);
    }
}
