package tz.go.mnrt.asert.modules.setup.financialyear.repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import tz.go.mnrt.asert.modules.core.repo.BaseRepository;
import tz.go.mnrt.asert.modules.setup.financialyear.entity.FinancialYear;

public interface FinancialYearRepository
        extends BaseRepository<FinancialYear, Long> {

    Optional<FinancialYear> findByUuid(UUID uuid);

    void deleteByUuid(UUID uuid);

    Optional<FinancialYear> findFirstByIsCurrentTrue();

    @Modifying
    @Query("UPDATE FinancialYear y set y.isCurrent= false where y.id <>:currentId ")
    void setNotCurrentOthers(@Param("currentId") Long currentId);

    @Query("Select y from FinancialYear y where (:startDate between y.startDate and y.endDate) or"
            + " (:endDate between y.startDate and y.endDate)")
    Optional<FinancialYear> existsByDate(
            @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT y FROM FinancialYear y WHERE " +
            "(y.startDate <= :endDate AND y.endDate >= :startDate)")
    Optional<FinancialYear> existByDate(
            @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
