package tz.go.mnrt.asert.modules.authority.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tz.go.mnrt.asert.modules.authority.dtos.AuthorityDto;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface AuthorityService {

    Page<AuthorityDto> findAll(Pageable page, Map<String, String> search);

    AuthorityDto findByUuid(UUID uuid);

    Set<Map<String, Object>> getAuthorityPermissions();

    void delete(UUID uuid);
}
