package tz.go.mnrt.asert.modules.setup.notificationtype.services;

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
import tz.go.mnrt.asert.modules.setup.notificationtype.dtos.NotificationTypeDto;
import tz.go.mnrt.asert.modules.setup.notificationtype.entity.NotificationType;
import tz.go.mnrt.asert.modules.setup.notificationtype.repository.NotificationTypeRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationTypeServiceImpl extends SimpleSearchService<NotificationType> implements NotificationTypeService {
  private final NotificationTypeRepository notificationTypeRepository;

  @Override
  public NotificationTypeDto save(NotificationTypeDto notificationTypeDto) {
    NotificationType notificationType = new NotificationType();
    if (notificationTypeDto.getUuid() != null) {
      notificationType =
          notificationTypeRepository
              .findByUuid(notificationTypeDto.getUuid())
              .orElseThrow(
                  () ->
                      new ValidationException(
                          "NotificationType with uuid {" + notificationTypeDto.getUuid() + "} not found"));
    }
    BeanUtils.copyProperties(notificationTypeDto, notificationType, "uuid");
    assert (notificationType.getUuid() != null);
    notificationType = notificationTypeRepository.save(notificationType);
    notificationTypeDto.setId(notificationType.getId());
    return notificationTypeDto;
  }

  @Override
  public Page<NotificationTypeDto> findAll(Pageable page, Map<String, String> search) {
    log.info("Loading paginated NotificationTypes with page {} and search {} ", page, search);
    return notificationTypeRepository
        .findAll(createSpecification(NotificationType.class, search), page)
        .map(NotificationTypeDto::new);
  }

  @Override
  public NotificationTypeDto findByUuid(UUID uuid) {
    log.info("finding role with uuid {} ", uuid);
    return notificationTypeRepository
        .findByUuid(uuid)
        .map(NotificationTypeDto::new)
        .orElseThrow(() -> new ValidationException("NotificationType with uuid {" + uuid + "} not found"));
  }

  @Override
  public void delete(UUID uuid) {
    log.info("deleting NotificationType with uuid {} ", uuid);
    notificationTypeRepository.softDelete(uuid);
  }
}
