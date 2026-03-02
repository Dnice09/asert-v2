package tz.go.mnrt.asert.modules.contactperson.repository;

import java.util.Optional;
import java.util.UUID;

import tz.go.mnrt.asert.modules.contactperson.entity.ContactPerson;
import tz.go.mnrt.asert.modules.core.repo.BaseRepository;

public interface ContactPersonRepository
        extends BaseRepository<ContactPerson, Long> {

    Optional<ContactPerson> findByUuid(UUID uuid);

    Optional<ContactPerson> findById(Long id);

    void deleteByUuid(UUID uuid);
}
