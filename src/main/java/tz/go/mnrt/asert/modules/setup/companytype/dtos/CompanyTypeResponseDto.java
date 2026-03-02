package tz.go.mnrt.asert.modules.setup.companytype.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.validation.constraints.NotNull;

import lombok.*;
import org.springframework.beans.BeanUtils;
import tz.go.mnrt.asert.modules.setup.companytype.entity.CompanyType;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CompanyTypeResponseDto implements Serializable {
    private Long id;

    private UUID uuid;

    private String name;

    private String code;

    private Boolean isActive;


    @NotNull(message = "Created Date is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime createdDate;

    public CompanyTypeResponseDto(CompanyType companyType) {
        BeanUtils.copyProperties(companyType, this);
    }
}
