package tz.go.mnrt.asert.modules.setup.notificationtype.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tz.go.mnrt.asert.modules.setup.notificationtype.dtos.NotificationTypeDto;

import java.util.Map;
import java.util.UUID;

public interface NotificationTypeService {

  NotificationTypeDto save(NotificationTypeDto notificationTypeDto);

  Page<NotificationTypeDto> findAll(Pageable page, Map<String, String> search);

  NotificationTypeDto findByUuid(UUID id);

  void delete(UUID uuid);
}
