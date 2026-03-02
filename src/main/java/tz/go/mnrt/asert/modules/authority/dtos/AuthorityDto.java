package tz.go.mnrt.asert.modules.authority.dtos;

import lombok.*;
import org.springframework.beans.BeanUtils;
import tz.go.mnrt.asert.modules.authority.entity.Authority;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AuthorityDto implements Serializable {
    private Long id;
    private UUID uuid;
    private String name;
    private String resource;
    private String action;

    public AuthorityDto(Authority authority) {
        BeanUtils.copyProperties(authority, this);
    }

    public static AuthorityDto fromRoleAuthority(Authority authority) {
        AuthorityDto dto = new AuthorityDto();
        BeanUtils.copyProperties(authority, dto);
        return dto;
    }
}
