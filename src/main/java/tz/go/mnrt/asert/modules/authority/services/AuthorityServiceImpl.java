package tz.go.mnrt.asert.modules.authority.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tz.go.mnrt.asert.modules.authority.repository.AuthorityRepository;
import tz.go.mnrt.asert.modules.core.services.SimpleSearchService;
import tz.go.mnrt.asert.modules.authority.dtos.AuthorityDto;
import tz.go.mnrt.asert.modules.authority.entity.Authority;

import javax.validation.ValidationException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthorityServiceImpl extends SimpleSearchService<Authority> implements AuthorityService {

    private final AuthorityRepository authorityRepository;

    @Override
    public Page<AuthorityDto> findAll(Pageable page, Map<String, String> search) {
        log.info("Loading paginated roles with page {} and search {} ", page, search);
        return authorityRepository.findAll(createSpecification(Authority.class, search), page).map(AuthorityDto::new);
    }

    @Override
    public AuthorityDto findByUuid(UUID uuid) {
        log.info("finding role with uuid {} ", uuid);
        return authorityRepository.findByUuid(uuid).map(AuthorityDto::new).orElseThrow(() -> new ValidationException("Role with uuid {" + uuid + "} not found"));
    }

    @Override
    public Set<Map<String, Object>> getAuthorityPermissions() {
        Set<Map<String, Object>> outerWrapper = new HashSet<>();

        List<Authority> distinctResources = authorityRepository.findDistinctByResourceNotNull();
        distinctResources.forEach(authority -> {
            Map<String, Object> innerWrapper = new HashMap<>();
            List<Authority> listAuthorities = authorityRepository.findAuthoritiesByResource(authority.getResource());
            innerWrapper.put("resource", authority.getResource());
            innerWrapper.put("id", authority.getId());
            innerWrapper.put("entries", listAuthorities);
            outerWrapper.add(innerWrapper);
        });

        return outerWrapper;
    }

    @Override
    public void delete(UUID uuid) {
        log.info("deleting role with uuid {} ", uuid);
        authorityRepository.deleteByUuid(uuid);
    }
}
