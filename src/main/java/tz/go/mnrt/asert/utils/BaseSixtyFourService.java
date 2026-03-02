package tz.go.mnrt.asert.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

import jakarta.xml.bind.DatatypeConverter;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@Slf4j
public class BaseSixtyFourService {
    @Value("${asert.uploaded-files.url}")
    private String uploadedFilesUrl;

    public String fileToBase64(String fileName) throws IOException {
        // Create the path of the files if the path doesn't exist
        byte[] encoded = null;
        String base64Prefix = null;
        if (Files.exists(Paths.get(fileName))) {
            File file = new File(fileName);
            encoded = Base64.encodeBase64(FileUtils.readFileToByteArray(file));
            int i = fileName.lastIndexOf('.');
            if (i > 0) {
                String extension = fileName.substring(i + 1);
                switch (extension) {// check image's extension
                    case "jpeg":
                    case "jpg":
                        base64Prefix = "data:image/jpeg;base64";
                        break;
                    case "png":
                        base64Prefix = "data:image/png;base64";
                        break;
                    case "pdf":
                        base64Prefix = "data:application/pdf;base64";
                        break;
                    case "doc":
                        base64Prefix = "data:application/msword;base64";
                        break;
                    case "xls":
                        base64Prefix = "data:application/vnd.ms-excel;base64";
                        break;
                    case "xlsx":
                        base64Prefix = "data:application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;base64";
                        break;
                    case "docx":
                        base64Prefix = "data:application/vnd.openxmlformats-officedocument.wordprocessingml.document;base64";
                        break;
                    default:// should write cases for more images types
                        base64Prefix = "";
                        log.error("The uploaded file is not of an allowed format");
                        break;
                }
            }
            log.info("File encoded to base 64 successfully");
        }
        assert encoded != null;
        String result = new String(encoded, StandardCharsets.US_ASCII);
        result = base64Prefix + "," + result;
        return result;
    }

    public String base64ToFile(String base64String) {
        String[] strings = base64String.split(",");
        String extension;
        switch (strings[0]) {// check image's extension
            case "data:image/jpeg;base64":
                extension = "jpeg";
                break;
            case "data:image/png;base64":
                extension = "png";
                break;
            case "data:application/pdf;base64":
                extension = "pdf";
                break;
            case "data:application/msword;base64":
                extension = "doc";
                break;
            case "data:application/vnd.ms-excel;base64":
                extension = "xls";
                break;
            case "data:application/vnd.openxmlformats-officedocument.wordprocessingml.document;base64":
                extension = "docx";
                break;
            case "data:application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;base64":
                extension = "xlsx";
                break;
            default:// should write cases for more images types
                extension = "";
                log.error("The uploaded file is not of an allowed format");
                break;
        }
        Instant now = Instant.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
        String fileName = now.atZone(java.time.ZoneId.of("UTC")).format(formatter);
        fileName = uploadedFilesUrl + "/" + fileName + "." + extension;

        // convert base64 string to binary data
        // Handle index out of range error
        if (strings.length == 2) {
            // Implies that the base 64 has not changed. Ignore
            byte[] data = DatatypeConverter.parseBase64Binary(strings[1]);
            File file = new File(fileName);
            try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file))) {
                assert data != null;
                outputStream.write(data);
                log.info("File uploaded successfully");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            fileName = null; // set file name to null if no change has been made to the base 64 file. i.e.
                             // existing file has been uploaded again
            log.info("The file uploaded has not changed. It will be ignored");
        }
        // byte[] data = DatatypeConverter.parseBase64Binary(strings[1]);

        return fileName;
    }

    public String saveFile(String base64, String filename) throws IOException {
        byte[] decodedBytes = Base64Utils.decodeFromString(base64);
        String extension = FilenameUtils.getExtension(filename);
        String randomFileName = UUID.randomUUID().toString() + "." + extension;
        File outputFile = Paths.get(uploadedFilesUrl, randomFileName).toFile();
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            fos.write(decodedBytes);
        }

        return randomFileName;
    }
}
