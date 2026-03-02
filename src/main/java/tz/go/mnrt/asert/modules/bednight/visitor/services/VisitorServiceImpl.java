package tz.go.mnrt.asert.modules.bednight.visitor.services;

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
import tz.go.mnrt.asert.modules.bednight.visitor.dtos.VisitorRequestDto;
import tz.go.mnrt.asert.modules.bednight.visitor.dtos.VisitorResponseDto;
import tz.go.mnrt.asert.modules.bednight.visitor.entity.Visitor;
import tz.go.mnrt.asert.modules.bednight.visitor.repository.VisitorRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class VisitorServiceImpl extends SimpleSearchService<Visitor> implements VisitorService {
  private final VisitorRepository visitorRepository;

  @Override
  public VisitorRequestDto save(VisitorRequestDto visitorRequestDto) {
    Visitor visitor = new Visitor();
    if (visitorRequestDto.getUuid() != null) {
      visitor =
          visitorRepository
              .findByUuid(visitorRequestDto.getUuid())
              .orElseThrow(
                  () ->
                      new ValidationException(
                          "Visitor with uuid {" + visitorRequestDto.getUuid() + "} not found"));
    }
    BeanUtils.copyProperties(visitorRequestDto, visitor, "uuid");
    assert (visitor.getUuid() != null);
    visitor = visitorRepository.save(visitor);
    visitorRequestDto.setId(visitor.getId());
    return visitorRequestDto;
  }

  @Override
  public Page<VisitorResponseDto> findAll(Pageable page, Map<String, String> search) {
    log.info("Loading paginated Visitors with page {} and search {} ", page, search);
    return visitorRepository
        .findAll(createSpecification(Visitor.class, search), page)
        .map(VisitorResponseDto::new);
  }

  @Override
  public VisitorResponseDto findByUuid(UUID uuid) {
    log.info("finding Visitor with uuid {} ", uuid);
    return visitorRepository
        .findByUuid(uuid)
        .map(VisitorResponseDto::new)
        .orElseThrow(() -> new ValidationException("Visitor with uuid {" + uuid + "} not found"));
  }

  @Override
  public void delete(UUID uuid) {
    log.info("deleting Visitor with uuid {} ", uuid);
    visitorRepository.softDelete(uuid);
  }
}
