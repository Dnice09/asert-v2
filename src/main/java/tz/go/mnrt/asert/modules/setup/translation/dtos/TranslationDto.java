package tz.go.mnrt.asert.modules.setup.translation.dtos;

import lombok.*;
import org.springframework.beans.BeanUtils;
import tz.go.mnrt.asert.modules.setup.translation.entity.Translation;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Valid
public class TranslationDto implements Serializable {
    private Long id;
    private UUID uuid;

    @NotNull(message = "Field Display Name is required")
    private String displayLabel;

    @NotNull(message = "English translation is required")
    private String english;

    @NotNull(message = "Swahili translation is required")
    private String swahili;

    public TranslationDto(Translation translation) {
        BeanUtils.copyProperties(translation, this);
    }
}
