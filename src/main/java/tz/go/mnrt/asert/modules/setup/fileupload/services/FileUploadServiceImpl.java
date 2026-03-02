package tz.go.mnrt.asert.modules.setup.fileupload.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tz.go.mnrt.asert.modules.core.rest.response.CustomApiResponse;
import tz.go.mnrt.asert.modules.core.services.SimpleSearchService;
import tz.go.mnrt.asert.modules.setup.fileupload.dtos.FileUpdateNameDto;
import tz.go.mnrt.asert.modules.setup.fileupload.dtos.FileUploadListDto;
import tz.go.mnrt.asert.modules.setup.fileupload.entity.FileUpload;
import tz.go.mnrt.asert.modules.setup.fileupload.enums.FileUploadType;
import tz.go.mnrt.asert.modules.setup.fileupload.repository.FileUploadRepository;

import javax.validation.ValidationException;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileUploadServiceImpl extends SimpleSearchService<FileUpload>
    implements FileUploadService {
    private final FileUploadRepository fileUploadRepository;

    @Value("${asert.uploaded-files.url}")
    private String pathToFileUploadFolder;

    public File getRawFileById(Long id) {
        FileUploadListDto upload = fileUploadRepository
            .findById(id)
            .map(FileUploadListDto::new)
            .orElseThrow(
                () -> new ValidationException("FileUpload with id {" + id + "} not found"));
        StringBuilder file_ext = new StringBuilder("");
        file_ext.append(pathToFileUploadFolder);
        file_ext.append("/");
        file_ext.append(upload.getFilePath());
        return new File(file_ext.toString());
    }

    public File getRawFileByUuid(UUID uuid) {
        FileUploadListDto upload = fileUploadRepository
            .findByUuid(uuid)
            .map(FileUploadListDto::new)
            .orElseThrow(
                () -> new ValidationException("FileUpload with uuid {" + uuid + "} not found"));
        StringBuilder file_ext = new StringBuilder("");
        file_ext.append(pathToFileUploadFolder);
        file_ext.append("/");
        file_ext.append(upload.getFilePath());
        File local = new File(file_ext.toString());
        return local;
    }

    @Override
    public FileUploadListDto save(String uploadType, MultipartFile file) throws ValidationException {
        FileUpload fileUpload = new FileUpload();

        if (isNotValidUploadType(uploadType)) {
            CustomApiResponse.badRequest(
                "File upload type is not valid, it should be either one of these"
                    + FileUploadType.values(),
                null);

            throw new ValidationException(
                "File upload type is not valid, it should be either one of these"
                    + FileUploadType.values());
        }
        // Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        fileUpload.setName(file.getOriginalFilename());
        fileUpload.setUserId(null);
        fileUpload.setFileSize(file.getSize());
        Pattern p = Pattern.compile("\\.[\\s\\S]+");
        Matcher m = p.matcher(file.getOriginalFilename());
        StringBuilder file_ext = new StringBuilder("");
        file_ext.append(pathToFileUploadFolder);
        file_ext.append("/");
        if (!Files.isDirectory(Paths.get(file_ext.toString()))) {
            throw new ValidationException(
                "File processing failed. Error: path not available = " + file_ext.toString());
        }
        file_ext.append(UUID.randomUUID().toString().replaceAll("-", ""));
        while (m.find())
            file_ext.append(m.group());

        File local = new File(file_ext.toString());
        try {
            file.transferTo(local);
        } catch (Exception exception) {
            throw new ValidationException("File processing failed. Error: " + exception.getMessage());
        }
        fileUpload.setFilePath(local.getName());
        fileUpload.setFileType(file.getContentType());
        fileUpload.setUploadType(uploadType);
        fileUpload = fileUploadRepository.save(fileUpload);
        FileUploadListDto fileUploadListDto = new FileUploadListDto();
        fileUpload.toDao(fileUploadListDto);
        return fileUploadListDto;
    }

    @Override
    public FileUploadListDto updateFileName(FileUpdateNameDto fileUpdateNameDto) {

        FileUpload fileUpload = new FileUpload();

        if (isNotValidUploadType(fileUpdateNameDto.getUploadType())) {
            CustomApiResponse.badRequest(
                "File upload type is not valid, it should be either one of these"
                    + FileUploadType.values(),
                null);

            throw new ValidationException(
                "File upload type is not valid, it should be either one of these"
                    + FileUploadType.values());
        }

        if (fileUpdateNameDto.getUuid() != null) {
            fileUpload = fileUploadRepository
                .findByUuid(fileUpdateNameDto.getUuid())
                .orElseThrow(
                    () -> new ValidationException(
                        "Role with uuid {" + fileUpdateNameDto.getUuid() + "} not found"));
        }

        fileUpload.setName(fileUpdateNameDto.getName());
        fileUpload = fileUploadRepository.save(fileUpload);

        FileUploadListDto fUploadListDto = new FileUploadListDto();

        BeanUtils.copyProperties(fileUpload, fUploadListDto);

        return fUploadListDto;
    }

    @Override
    public Page<FileUploadListDto> findAll(Pageable page, Map<String, String> search) {
        log.info("Loading paginated FileUploads with page {} and search {} ", page, search);
        return fileUploadRepository
            .findAll(createSpecification(FileUpload.class, search), page)
            .map(FileUploadListDto::new);
    }

    @Override
    public FileUploadListDto findByUuid(UUID uuid) {
        log.info("finding upload with uuid {} ", uuid);
        return fileUploadRepository
            .findByUuid(uuid)
            .map(FileUploadListDto::new)
            .orElseThrow(
                () -> new ValidationException("FileUpload with uuid {" + uuid + "} not found"));
    }

    @Override
    public void delete(UUID uuid) {
        log.info("deleting FileUpload with uuid {} ", uuid);
        fileUploadRepository.destroy(uuid);
    }

    private boolean isNotValidUploadType(String uploadType) {
        return !Arrays.stream(FileUploadType.values())
            .anyMatch(FileUploadType.valueOf(uploadType)::equals);
    }

    @Override
    public String convertFileToBase64(File file) {
        try {
            if (file.exists()) {
                byte[] bytes = FileUtils.readFileToByteArray(file);
                return Base64.getEncoder().encodeToString(bytes);
            } else {
                String uploadDirectory = pathToFileUploadFolder;
                File notFoundFile = new File(uploadDirectory, "no-file.pdf");
                byte[] bytes = FileUtils.readFileToByteArray(notFoundFile);
                return Base64.getEncoder().encodeToString(bytes);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            byte[] bytesEncoded = Base64.getEncoder().encode("No file found".getBytes());
            return Base64.getEncoder().encodeToString(bytesEncoded);
        }
    }

    @Override
    public String getExtension(String filename) {
        return FilenameUtils.getExtension(filename);
    }
}
