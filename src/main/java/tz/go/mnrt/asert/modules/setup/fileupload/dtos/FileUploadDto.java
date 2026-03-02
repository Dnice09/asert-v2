package tz.go.mnrt.asert.modules.setup.fileupload.dtos;

import java.util.UUID;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tz.go.mnrt.asert.modules.core.dtos.BaseDto;
import tz.go.mnrt.asert.modules.setup.fileupload.entity.FileUpload;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class FileUploadDto extends BaseDto {
    private Long id;
    private UUID uuid;

    @NotNull(message = "Name is required")
    private String name;

    public FileUploadDto(FileUpload fileUpload) {
        fileUpload.toDao(this);
    }
}
