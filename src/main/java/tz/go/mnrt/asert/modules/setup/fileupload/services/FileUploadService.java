package tz.go.mnrt.asert.modules.setup.fileupload.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import tz.go.mnrt.asert.modules.setup.fileupload.dtos.FileUpdateNameDto;
import tz.go.mnrt.asert.modules.setup.fileupload.dtos.FileUploadListDto;

import javax.validation.ValidationException;
import java.io.File;
import java.util.Map;
import java.util.UUID;

public interface FileUploadService {

    FileUploadListDto save(String uploadType, MultipartFile file) throws ValidationException;

    Page<FileUploadListDto> findAll(Pageable page, Map<String, String> search);

    FileUploadListDto findByUuid(UUID id);

    FileUploadListDto updateFileName(FileUpdateNameDto fileUpdateNameDto);

    File getRawFileByUuid(UUID uuid);

    File getRawFileById(Long id);

    void delete(UUID uuid);

    String convertFileToBase64(File file);

    String getExtension(String filename);
}
