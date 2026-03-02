package tz.go.mnrt.asert.modules.setup.fileupload.dtos;

import java.util.UUID;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Valid
public class FileUpdateNameDto {
    @NotNull
    private String name;

    private UUID uuid;

    private Long id;

    private Long fileSize;

    private String uploadType;

    private String fileType;

    private String size;
}
