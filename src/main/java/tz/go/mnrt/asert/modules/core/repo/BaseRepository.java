package tz.go.mnrt.asert.modules.core.repo;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseRepository<T, ID> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

    @SuppressWarnings("HQL_SYNTAX")
    @Modifying
    @Query("UPDATE #{#entityName} e SET e.isDeleted = true WHERE e.uuid = ?1")
    void softDelete(UUID id);

    @Modifying
    @Query("UPDATE #{#entityName} e SET e.isDeleted = true WHERE e.uuid = ?1")
    void softDeleteByUuid(UUID id);

    @Modifying
    @Query("UPDATE #{#entityName} e SET e.isDeleted = true WHERE e.uuid = ?1")
    void deleteByUuid(UUID id);

    @Modifying
    @Query("DELETE #{#entityName} e WHERE e.uuid = ?1")
    void destroy(UUID id);

    @Modifying
    @Query("UPDATE #{#entityName} e SET e.isDeleted = true WHERE e.id = ?1")
    void softDeleteById(Long id);
}
