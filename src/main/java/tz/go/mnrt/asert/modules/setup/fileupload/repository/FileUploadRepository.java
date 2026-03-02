package tz.go.mnrt.asert.modules.setup.fileupload.repository;

import java.util.Optional;
import java.util.UUID;

import tz.go.mnrt.asert.modules.core.repo.BaseRepository;
import tz.go.mnrt.asert.modules.setup.fileupload.entity.FileUpload;

public interface FileUploadRepository extends BaseRepository<FileUpload, Long> {

  Optional<FileUpload> findByUuid(UUID uuid);

  void deleteByUuid(UUID uuid);
}
