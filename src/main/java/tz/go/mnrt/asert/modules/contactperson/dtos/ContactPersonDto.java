package tz.go.mnrt.asert.modules.contactperson.dtos;

import java.io.Serializable;
import java.util.UUID;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.BeanUtils;

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
@Valid
public class ContactPersonDto implements Serializable {
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

    public ContactPersonDto(ContactPerson contactPerson) {
        BeanUtils.copyProperties(contactPerson, this);
    }
}
