package tz.go.mnrt.asert.modules.company.services;

import java.util.Map;
import java.util.UUID;
import javax.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tz.go.mnrt.asert.modules.core.services.SimpleSearchService;
import tz.go.mnrt.asert.modules.company.dtos.CompanyDto;
import tz.go.mnrt.asert.modules.company.entity.Company;
import tz.go.mnrt.asert.modules.company.repository.CompanyRepository;
import tz.go.mnrt.asert.modules.user.dtos.LoggedInUserDto;
import tz.go.mnrt.asert.modules.user.entity.User;
import tz.go.mnrt.asert.modules.user.service.UserService;
import tz.go.mnrt.asert.modules.user.repository.UserRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class CompanyServiceImpl extends SimpleSearchService<Company> implements CompanyService {
    private final CompanyRepository companyRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    @Override
    public CompanyDto save(CompanyDto companyDto) {
        Company company = new Company();
        LoggedInUserDto logged = userService.loggedIn().orElse(null);

        if (logged == null) {
            log.error("User info not found");
            throw new ValidationException("User info not found");
        }

        User user = userRepository.findByUuid(logged.getUuid())
                .orElseThrow(() -> new ValidationException("User with uuid {" + logged.getUuid() + "} not found"));

        if (companyDto.getUuid() != null) {
            company = companyRepository
                    .findByUuid(companyDto.getUuid())
                    .orElseThrow(
                            () -> new ValidationException(
                                    "Company with uuid {" + companyDto.getUuid() + "} not found"));
        }
        BeanUtils.copyProperties(companyDto, company, "uuid");
        assert (company.getUuid() != null);
        company = companyRepository.save(company);

        if (company.getId() == null) {
            log.error("Company not saved");
            throw new ValidationException("Company not saved");
        }

        if (company.getId() != null) {
            user.setCompanyId(company.getId());
            userRepository.save(user);
        }

        companyDto.setId(company.getId());
        return companyDto;
    }

    @Override
    public Page<CompanyDto> findAll(Pageable page, Map<String, String> search) {
        log.info("Loading paginated Companys with page {} and search {} ", page, search);
        return companyRepository
                .findAll(createSpecification(Company.class, search), page)
                .map(CompanyDto::new);
    }

    @Override
    public CompanyDto findByUuid(UUID uuid) {
        log.info("finding role with uuid {} ", uuid);
        return companyRepository
                .findByUuid(uuid)
                .map(CompanyDto::new)
                .orElseThrow(() -> new ValidationException("Company with uuid {" + uuid + "} not found"));
    }

    @Override
    public void delete(UUID uuid) {
        log.info("deleting Company with uuid {} ", uuid);
        companyRepository.softDelete(uuid);
    }

    @Override
    public CompanyDto findById(Long id) {
        log.info("finding role with id {} ", id);
        return companyRepository
                .findById(id)
                .map(CompanyDto::new)
                .orElseThrow(() -> new ValidationException("Company with id {" + id + "} not found"));
    }
}
