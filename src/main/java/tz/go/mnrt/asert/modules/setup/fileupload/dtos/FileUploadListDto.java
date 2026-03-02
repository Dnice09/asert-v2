package tz.go.mnrt.asert.modules.setup.fileupload.dtos;

import java.util.UUID;
import lombok.*;
import tz.go.mnrt.asert.modules.setup.fileupload.entity.FileUpload;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class FileUploadListDto {
  private Long id;
  private UUID uuid;

  private String name;

  private String uploadType;

  private String filePath;

  private String fileType;

  private Long fileSize;

  public FileUploadListDto(FileUpload fileUpload) {
    fileUpload.toDao(this);
  }
}
