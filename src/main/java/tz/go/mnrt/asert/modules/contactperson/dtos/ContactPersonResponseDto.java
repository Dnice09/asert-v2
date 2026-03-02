package tz.go.mnrt.asert.modules.contactperson.dtos;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tz.go.mnrt.asert.modules.contactperson.entity.ContactPerson;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ContactPersonResponseDto implements Serializable {
    private Long id;
    private UUID uuid;

    @NotNull
    private String name;

    private String address;

    @NotNull
    private String email;

    @NotNull
    private String phone;

    private String physicalAddress;

    private String fax;

    @NotNull
    private Boolean isPrimary;

    @NotNull
    private Long companyId;

    @NotNull(message = "Created Date is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime createdAt;

    public ContactPersonResponseDto(ContactPerson contactPerson) {
        BeanUtils.copyProperties(contactPerson, this);
    }
}
