package tz.go.mnrt.asert.modules.setup.fileupload.rest;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.ValidationException;

import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import tz.go.mnrt.asert.configs.NoAuthorization;
import tz.go.mnrt.asert.constants.Constant;
import tz.go.mnrt.asert.modules.core.rest.response.CustomApiResponse;
import tz.go.mnrt.asert.modules.setup.fileupload.dtos.FileUpdateNameDto;
import tz.go.mnrt.asert.modules.setup.fileupload.dtos.FileUploadListDto;
import tz.go.mnrt.asert.modules.setup.fileupload.services.FileUploadService;

@RestController
@RequestMapping(Constant.API_V1 + "/uploads")
@RequiredArgsConstructor
public class FileUploadResource {

    final FileUploadService fileUploadService;

    @GetMapping()
    public CustomApiResponse get(Pageable pagination, @RequestParam() Map<String, String> search) {

        return CustomApiResponse.ok(
                fileUploadService.findAll(
                        PageRequest.of(
                                pagination.getPageSize(),
                                pagination.getPageSize(),
                                pagination.getSortOr(Sort.by("id").descending())),
                        search));
    }

    @PostMapping
    @Transactional
    public CustomApiResponse create(
            @Valid @RequestParam("uploadType") String uploadType,
            @RequestParam(value = "file") MultipartFile file) {

        FileUploadListDto dto = fileUploadService.save(uploadType, file);
        return CustomApiResponse.created(uploadType + "_" + Constant.UPLOAD_SUCCESS, dto);
    }

    @PutMapping("/{uuid}")
    @Transactional
    public CustomApiResponse update(@Valid @RequestParam("uploadType") String uploadType,
            @Valid @RequestParam(value = "file") MultipartFile file, @PathVariable UUID uuid) {

        return CustomApiResponse.accepted("File Updated successfully",
                fileUploadService.save(uploadType, file));
    }

    @PutMapping("/{uuid}/update-name")
    @Transactional
    public CustomApiResponse updateName(@Valid @RequestBody FileUpdateNameDto fileUpdateNameDto,
            @PathVariable UUID uuid) {
        if (fileUpdateNameDto.getUuid() == null || !Objects.equals(fileUpdateNameDto.getUuid(), uuid)) {
            throw new ValidationException("File uuid must be present and equals to path id {" + uuid + "}");
        }

        return CustomApiResponse.ok("File Updated successfully", fileUploadService.updateFileName(fileUpdateNameDto));
    }

    @DeleteMapping("/{uuid}")
    @Transactional
    public CustomApiResponse delete(@PathVariable(value = "uuid") UUID uuid) {
        fileUploadService.delete(uuid);
        return CustomApiResponse.ok(Constant.DELETE_SUCCESS);
    }

    @GetMapping("/{id}/view")
    @Transactional
    @NoAuthorization
    public ResponseEntity<InputStreamResource> view(@PathVariable(value = "id") String id)
            throws Exception {
        try {
            File file = null;
            if (id.chars().allMatch(Character::isDigit)) {
                file = fileUploadService.getRawFileById(Long.parseLong(id));
            } else {
                file = fileUploadService.getRawFileByUuid(UUID.fromString(id));
            }
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getName() + "\"");
            // Enable caching for better performance (cache for 1 hour)
            headers.add("Cache-Control", "public, max-age=3600");
            headers.add("Content-Type", Files.probeContentType(file.toPath()));
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
            return ResponseEntity.ok().headers(headers).contentLength(file.length()).body(resource);
        } catch (Exception ex) {
            throw new Exception("File not found with id/uuid = " + id);
        }
    }
}
