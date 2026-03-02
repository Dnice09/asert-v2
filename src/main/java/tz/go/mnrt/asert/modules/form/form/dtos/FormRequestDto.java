package tz.go.mnrt.asert.modules.form.form.dtos;

import lombok.*;
import tz.go.mnrt.asert.modules.form.form.entity.Form;
import tz.go.mnrt.asert.modules.form.formsection.dtos.FormSectionRequestDto;
import tz.go.mnrt.asert.modules.hotel.hotel.enums.PropertyType;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class FormRequestDto {
    private Long id;
    private UUID uuid;

    private String name;

    private String description;

    private Set<PropertyType> propertyTypes;

    private List<FormSectionRequestDto> sections;

    public FormRequestDto(Form entity) {
        entity.toDao(this);
    }
}
