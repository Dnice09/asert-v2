package tz.go.mnrt.asert.modules.setup.country.services;

import java.util.Map;
import java.util.UUID;

import javax.validation.ValidationException;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tz.go.mnrt.asert.modules.core.services.SimpleSearchService;
import tz.go.mnrt.asert.modules.setup.country.dtos.CountryRequestDto;
import tz.go.mnrt.asert.modules.setup.country.dtos.CountryResponseDto;
import tz.go.mnrt.asert.modules.setup.country.entity.Country;
import tz.go.mnrt.asert.modules.setup.country.repository.CountryRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class CountryServiceImpl extends SimpleSearchService<Country> implements CountryService {
  private final CountryRepository countryRepository;

  @Override
  public CountryRequestDto save(CountryRequestDto countryRequestDto) {
    Country country = new Country();
    if (countryRequestDto.getUuid() != null) {
      country =
          countryRepository
              .findByUuid(countryRequestDto.getUuid())
              .orElseThrow(
                  () ->
                      new ValidationException(
                          "Country with uuid {" + countryRequestDto.getUuid() + "} not found"));
    }
    BeanUtils.copyProperties(countryRequestDto, country, "uuid");
    assert (country.getUuid() != null);
    country = countryRepository.save(country);
    countryRequestDto.setId(country.getId());
    return countryRequestDto;
  }

  @Override
  public Page<CountryResponseDto> findAll(Pageable page, Map<String, String> search) {
    log.info("Loading paginated Countrys with page {} and search {} ", page, search);
    return countryRepository
        .findAll(createSpecification(Country.class, search), page)
        .map(CountryResponseDto::new);
  }

  @Override
  public CountryResponseDto findByUuid(UUID uuid) {
    log.info("finding role with uuid {} ", uuid);
    return countryRepository
        .findByUuid(uuid)
        .map(CountryResponseDto::new)
        .orElseThrow(() -> new ValidationException("Country with uuid {" + uuid + "} not found"));
  }

  @Override
  public void delete(UUID uuid) {
    log.info("deleting Country with uuid {} ", uuid);
    countryRepository.softDelete(uuid);
  }
}
