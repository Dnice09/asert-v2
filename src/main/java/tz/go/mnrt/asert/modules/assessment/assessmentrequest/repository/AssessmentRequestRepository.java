package tz.go.mnrt.asert.modules.assessment.assessmentrequest.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tz.go.mnrt.asert.modules.assessment.assessmentrequest.entity.AssessmentRequest;
import tz.go.mnrt.asert.modules.assessment.assessmentrequest.entity.AssessmentRequestStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AssessmentRequestRepository extends JpaRepository<AssessmentRequest, Long>, JpaSpecificationExecutor<AssessmentRequest> {

    Optional<AssessmentRequest> findByUuid(UUID uuid);

    @Query("SELECT DISTINCT ar FROM AssessmentRequest ar " +
           "LEFT JOIN FETCH ar.essentialItems " +
           "WHERE ar.uuid = :uuid")
    Optional<AssessmentRequest> findByUuidWithEssentialItems(@Param("uuid") UUID uuid);

    Page<AssessmentRequest> findByStatus(AssessmentRequestStatus status, Pageable pageable);

    Page<AssessmentRequest> findBySubmittedByUser_Id(Long userId, Pageable pageable);

    @Query("SELECT ar FROM AssessmentRequest ar WHERE ar.submittedByUser.id = :userId AND ar.status = :status")
    Page<AssessmentRequest> findBySubmittedByUserIdAndStatus(@Param("userId") Long userId, 
                                                            @Param("status") AssessmentRequestStatus status, 
                                                            Pageable pageable);

    @Query("SELECT ar FROM AssessmentRequest ar WHERE ar.hotel.name LIKE %:hotelName%")
    Page<AssessmentRequest> findByHotelNameContainingIgnoreCase(@Param("hotelName") String hotelName, Pageable pageable);

    @Query("SELECT ar FROM AssessmentRequest ar WHERE ar.hotel.propertyType = :propertyType")
    Page<AssessmentRequest> findByHotelPropertyType(@Param("propertyType") String propertyType, Pageable pageable);

    @Query("SELECT ar FROM AssessmentRequest ar WHERE ar.submittedAt BETWEEN :startDate AND :endDate")
    Page<AssessmentRequest> findBySubmittedAtBetween(@Param("startDate") LocalDateTime startDate, 
                                                   @Param("endDate") LocalDateTime endDate, 
                                                   Pageable pageable);

    @Query("SELECT ar FROM AssessmentRequest ar WHERE ar.email = :email ORDER BY ar.submittedAt DESC")
    List<AssessmentRequest> findByEmailOrderBySubmittedAtDesc(@Param("email") String email);

    @Query("SELECT COUNT(ar) FROM AssessmentRequest ar WHERE ar.status = :status")
    long countByStatus(@Param("status") AssessmentRequestStatus status);

    @Query("SELECT COUNT(ar) FROM AssessmentRequest ar WHERE ar.submittedAt >= :startDate")
    long countSubmittedAfter(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT ar.hotel.propertyType, COUNT(ar) FROM AssessmentRequest ar GROUP BY ar.hotel.propertyType")
    List<Object[]> countByHotelPropertyType();

    @Query("SELECT ar.status, COUNT(ar) FROM AssessmentRequest ar GROUP BY ar.status")
    List<Object[]> countByStatus();

    @Query("SELECT ar FROM AssessmentRequest ar WHERE ar.status IN :statuses ORDER BY ar.submittedAt DESC")
    Page<AssessmentRequest> findByStatusInOrderBySubmittedAtDesc(@Param("statuses") List<AssessmentRequestStatus> statuses, Pageable pageable);

    @Query("SELECT ar FROM AssessmentRequest ar WHERE " +
           "(:hotelName IS NULL OR ar.hotel.name LIKE %:hotelName%) AND " +
           "(:propertyType IS NULL OR ar.hotel.propertyType = :propertyType) AND " +
           "(:status IS NULL OR ar.status = :status) AND " +
           "(:email IS NULL OR ar.email LIKE %:email%)")
    Page<AssessmentRequest> findWithFilters(@Param("hotelName") String hotelName,
                                          @Param("propertyType") String propertyType,
                                          @Param("status") AssessmentRequestStatus status,
                                          @Param("email") String email,
                                          Pageable pageable);

    boolean existsByEmailAndHotel_UuidAndStatus(String email, UUID hotelUuid, AssessmentRequestStatus status);
}