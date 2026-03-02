package tz.go.mnrt.asert.modules.setup.attachmenttype.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.validation.constraints.NotNull;

import lombok.*;
import org.springframework.beans.BeanUtils;
import tz.go.mnrt.asert.modules.setup.attachmenttype.entity.AttachmentType;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AttachmentTypeResponseDto implements Serializable {
    private Long id;

    private UUID uuid;

    @NotNull
    private String name;

    @NotNull
    private String code;

    @NotNull
    private Boolean isRequired;

    @NotNull
    private Boolean isActive;


    @NotNull(message = "Created Date is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime createdDate;

    public AttachmentTypeResponseDto(AttachmentType attachmentType) {
        BeanUtils.copyProperties(attachmentType, this);
    }
}
