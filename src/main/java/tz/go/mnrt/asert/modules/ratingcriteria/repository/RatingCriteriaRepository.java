package tz.go.mnrt.asert.modules.ratingcriteria.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import tz.go.mnrt.asert.modules.core.repo.BaseRepository;
import tz.go.mnrt.asert.modules.hotel.hotel.enums.PropertyType;
import tz.go.mnrt.asert.modules.ratingcriteria.entity.RatingCriteria;

public interface RatingCriteriaRepository
        extends BaseRepository<RatingCriteria, Long> {

    Optional<RatingCriteria> findByUuid(UUID uuid);

    void deleteByUuid(UUID uuid);

    // Find all rating criteria for a specific property type
    List<RatingCriteria> findByPropertyTypeAndIsDeletedFalseOrderByStarLevelAsc(PropertyType propertyType);

    // Find specific rating criteria by property type and star level
    Optional<RatingCriteria> findByPropertyTypeAndStarLevelAndIsDeletedFalse(PropertyType propertyType,
            Integer starLevel);

    // Find the appropriate star rating for a given score and property type
    @Query("SELECT rc FROM RatingCriteria rc WHERE rc.propertyType = :propertyType " +
            "AND :score >= rc.minScore AND :score <= rc.maxScore " +
            "AND rc.isDeleted = false")
    Optional<RatingCriteria> findByPropertyTypeAndScoreRange(@Param("propertyType") PropertyType propertyType,
            @Param("score") Double score);

    // Get the maximum star level available for a property type
    @Query("SELECT MAX(rc.starLevel) FROM RatingCriteria rc WHERE rc.propertyType = :propertyType AND rc.isDeleted = false")
    Optional<Integer> findMaxStarLevelByPropertyType(@Param("propertyType") PropertyType propertyType);

    // Get the minimum star level available for a property type
    @Query("SELECT MIN(rc.starLevel) FROM RatingCriteria rc WHERE rc.propertyType = :propertyType AND rc.isDeleted = false")
    Optional<Integer> findMinStarLevelByPropertyType(@Param("propertyType") PropertyType propertyType);

    // Check if a property type and star level combination already exists
    boolean existsByPropertyTypeAndStarLevelAndIsDeletedFalse(PropertyType propertyType, Integer starLevel);

    @Query("SELECT rc FROM RatingCriteria rc WHERE rc.propertyType = :propertyType " +
            "AND :score BETWEEN rc.minScore AND rc.maxScore " +
            "AND rc.isDeleted = false " +
            "ORDER BY rc.starLevel DESC")
    List<RatingCriteria> findTopByPropertyTypeAndScore(@Param("propertyType") PropertyType propertyType,
            @Param("score") Double score);
}
