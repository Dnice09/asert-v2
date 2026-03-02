package tz.go.mnrt.asert.modules.setup.attachmenttype.dtos;

import java.io.Serializable;
import java.util.UUID;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.*;
import org.springframework.beans.BeanUtils;
import tz.go.mnrt.asert.modules.setup.attachmenttype.entity.AttachmentType;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Valid
public class AttachmentTypeRequestDto implements Serializable {
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

    public AttachmentTypeRequestDto(AttachmentType attachmentType) {
        BeanUtils.copyProperties(attachmentType, this);
    }
}
