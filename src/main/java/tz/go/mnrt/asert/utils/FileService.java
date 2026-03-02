package tz.go.mnrt.asert.utils;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ValidationException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Service
public class FileService {

    @Value("${asert.uploaded-files.url}")
    private String uploadedFilesUrl;

    public Resource download(String filename) {
        try {
            Path file = Paths.get(uploadedFilesUrl).resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    public void exportExcel(HttpServletResponse response, String now, JasperPrint jasperPrint) throws JRException, IOException {
        JRXlsxExporter exporter = new JRXlsxExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        ByteArrayOutputStream reportStream = new ByteArrayOutputStream();
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(reportStream));
        SimpleXlsxReportConfiguration reportConfig = new SimpleXlsxReportConfiguration();
        reportConfig.setSheetNames(new String[]{"Data"});
        exporter.setConfiguration(reportConfig);
        exporter.exportReport();
        String fileName = now + "-download.xlsx";
        response.setContentType("application/vnd.ms-excel");
        response.addHeader("Content-Disposition", "inline; filename=" + fileName + ";");

        OutputStream responseOutputStream = response.getOutputStream();
        responseOutputStream.write(reportStream.toByteArray());
        responseOutputStream.close();
        reportStream.close();
    }

    public void exportDoc(HttpServletResponse response, String now, JasperPrint jasperPrint) throws JRException, IOException {
        JRDocxExporter exporter = new JRDocxExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        ByteArrayOutputStream reportStream = new ByteArrayOutputStream();
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(reportStream));
        exporter.exportReport();
        String fileName = now + "-download.docx";
        response.setContentType("application/octet-stream");
        response.addHeader("Content-Disposition", "inline; filename=" + fileName + ";");
        OutputStream responseOutputStream = response.getOutputStream();
        responseOutputStream.write(reportStream.toByteArray());
        responseOutputStream.close();
        reportStream.close();
    }

    public void exportPdf(JasperPrint jasperPrint, HttpServletResponse response, String receiptPdfFile) throws JRException, IOException {
        JRPdfExporter pdfExporter = new JRPdfExporter();
        pdfExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        ByteArrayOutputStream pdfReportStream = new ByteArrayOutputStream();
        pdfExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(pdfReportStream));
        pdfExporter.exportReport();

        response.setContentType("application/pdf");
        response.setHeader("Content-Length", String.valueOf(pdfReportStream.size()));
        response.addHeader("Content-Disposition", "inline; filename=" + receiptPdfFile + ";");

        OutputStream responseOutputStream = response.getOutputStream();
        responseOutputStream.write(pdfReportStream.toByteArray());
        responseOutputStream.close();
        pdfReportStream.close();
    }

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

    public void delete(String file) {
        Path path = FileSystems.getDefault().getPath(uploadedFilesUrl + "/" + file);
        try {
            Files.deleteIfExists(path);
        } catch (IOException x) {
            x.printStackTrace();
        }
    }

    public String uploadFile(String file, String fileName, String extension) {
        return doUpload(file, fileName, extension);
    }

    private String doUpload(String file, String fileName, String extension) {
        String fullFilePath;
        if (!new File(uploadedFilesUrl).exists()) {
            boolean success = new File(uploadedFilesUrl).mkdirs();
            if (!success) {
                System.out.println("Upload Directory Error");
            }
        }
        fullFilePath = fileName + "." + extension;
        File fileToCreate = new File(uploadedFilesUrl, fullFilePath);
        if (!fileToCreate.exists()) {
            byte[] decodedBytes = Base64.getDecoder().decode(file);
            try {
                FileUtils.writeByteArrayToFile(fileToCreate, decodedBytes);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
        return fullFilePath;
    }

    public String exportBase64(JasperPrint jasperPrint) throws JRException {
        byte[] pdf = JasperExportManager.exportReportToPdf(jasperPrint);
        return convertFileToBase64(pdf);
    }

    public static String convertFileToBase64(byte[] file) {
        return Base64.getEncoder().encodeToString(file);
    }

    /**
     * Checks if a file with the specified name exists in the given directory.
     *
     * @param directoryPath the path to the directory
     * @param fileName      the name of the file to check
     * @return true if the file exists in the directory, false otherwise
     */
    public boolean isFileInDirectory(String directoryPath, String fileName) {
        // Create a File object for the directory
        File directory = new File(directoryPath);

        // Check if the directory exists and is indeed a directory
        if (directory.exists() && directory.isDirectory()) {
            // List all files in the directory
            String[] files = directory.list();

            if (files != null) {
                for (String file : files) {
                    if (file.equals(fileName)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public String uploadPhoto(String base64Photo) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(base64Photo);

            String fileName = UUID.randomUUID().toString().replace("-", "") + ".jpg";
            Path uploadPath = Paths.get(uploadedFilesUrl);

            // Ensure /uploads folder exists
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(fileName);
            Files.write(filePath, decodedBytes);

            return fileName;

        } catch (IOException e) {
            throw new RuntimeException("Failed to save photo", e);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid base64 image data");
        }
    }
}
