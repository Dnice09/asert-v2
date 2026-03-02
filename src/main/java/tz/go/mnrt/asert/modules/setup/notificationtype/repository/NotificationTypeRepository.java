package tz.go.mnrt.asert.modules.setup.notificationtype.repository;

import java.util.Optional;
import java.util.UUID;
import tz.go.mnrt.asert.modules.core.repo.BaseRepository;
import tz.go.mnrt.asert.modules.setup.notificationtype.entity.NotificationType;

public interface NotificationTypeRepository
    extends BaseRepository<NotificationType, Long> {

  Optional<NotificationType> findByUuid(UUID uuid);

  void deleteByUuid(UUID uuid);

    Optional<NotificationType> findByCode(String code);
}
