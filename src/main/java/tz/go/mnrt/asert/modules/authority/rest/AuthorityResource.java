package tz.go.mnrt.asert.modules.authority.rest;

import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tz.go.mnrt.asert.constants.Constant;
import tz.go.mnrt.asert.modules.authority.services.AuthorityService;
import tz.go.mnrt.asert.modules.core.rest.response.CustomApiResponse;

@RestController
@RequestMapping(Constant.API_V1 + "/authorities")
@RequiredArgsConstructor
@Slf4j
public class AuthorityResource {

    private final AuthorityService authorityService;

    @GetMapping()
    public CustomApiResponse get(Pageable pagination, @RequestParam() Map<String, String> search) {

        return CustomApiResponse.ok(
                authorityService.findAll(
                        PageRequest.of(
                                pagination.getPageNumber(),
                                pagination.getPageSize(),
                                pagination.getSortOr(Sort.by("id").descending())),
                        search));
    }

    @GetMapping("/permissions")
    public CustomApiResponse getPermissions() {
        return CustomApiResponse.ok(authorityService.getAuthorityPermissions());
    }
}
