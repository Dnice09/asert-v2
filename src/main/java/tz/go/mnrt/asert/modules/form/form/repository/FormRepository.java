package tz.go.mnrt.asert.modules.form.form.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tz.go.mnrt.asert.modules.core.repo.BaseRepository;
import tz.go.mnrt.asert.modules.form.form.dtos.FormResponseDto;
import tz.go.mnrt.asert.modules.form.form.entity.Form;
import tz.go.mnrt.asert.modules.hotel.hotel.enums.PropertyType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FormRepository extends BaseRepository<Form, Long> {
    Optional<Form> findByUuid(UUID uuid);

    @Query("SELECT f FROM Form f WHERE f.uuid = :uuid AND f.isDeleted = false")
    Optional<Form> findByUuidWithAllDetails(@Param("uuid") UUID uuid);

    @Query("SELECT new tz.go.mnrt.asert.modules.form.form.dtos.FormResponseDto(f.uuid, f.name, f.description) " +
            "FROM Form f WHERE f.isDeleted = false")
    Page<FormResponseDto> findAllProjected(Pageable pageable);


    void deleteByUuid(UUID uuid);

    // Add method to find forms that contain a specific property type
    @Query("SELECT f FROM Form f JOIN f.propertyTypes pt WHERE pt = :propertyType")
    List<Form> findByPropertyTypesContaining(@Param("propertyType") PropertyType propertyType);

    // Add method to check if a form exists for a property type
    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM Form f JOIN f.propertyTypes pt WHERE pt = :propertyType")
    boolean existsByPropertyTypesContaining(@Param("propertyType") PropertyType propertyType);

    // Add method to find all forms except a specific one by UUID that contain a property type
    @Query("SELECT f FROM Form f JOIN f.propertyTypes pt WHERE pt = :propertyType AND f.uuid != :uuid")
    List<Form> findByPropertyTypesContainingAndUuidNot(@Param("propertyType") PropertyType propertyType, @Param("uuid") UUID uuid);

    // Add method to find form by property type where isDeleted is false
    @Query(value = "SELECT f.* FROM forms f JOIN form_property_types fpt ON f.id = fpt.form_id WHERE fpt.property_type = :propertyType AND f.is_deleted = false ORDER BY f.id ASC LIMIT 1", nativeQuery = true)
    Optional<Form> findByPropertyTypesContainingAndIsDeletedFalse(@Param("propertyType") String propertyType);
}
