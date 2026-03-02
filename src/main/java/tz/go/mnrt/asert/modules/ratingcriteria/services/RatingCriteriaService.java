package tz.go.mnrt.asert.modules.ratingcriteria.services;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import tz.go.mnrt.asert.modules.hotel.hotel.enums.PropertyType;
import tz.go.mnrt.asert.modules.ratingcriteria.dtos.RatingCriteriaRequestDto;
import tz.go.mnrt.asert.modules.ratingcriteria.dtos.RatingCriteriaResponseDto;

public interface RatingCriteriaService {

    RatingCriteriaRequestDto save(RatingCriteriaRequestDto ratingCriteriaDto);

    Page<RatingCriteriaResponseDto> findAll(Pageable page, Map<String, String> search);

    RatingCriteriaResponseDto findByUuid(UUID id);

    void delete(UUID uuid);

    // Legacy method - deprecated, use getStarRatingByScoreAndPropertyType instead
    @Deprecated
    String getStarRatingByScore(Double score);

    // Get star rating for a specific score and property type
    String getStarRatingByScoreAndPropertyType(Double score, PropertyType propertyType);

    // Get star rating for a specific score and property type
    String getTopStarRatingByPropertyTypeAndScore(Double score, PropertyType propertyType);

    // Get all rating criteria for a specific property type
    List<RatingCriteriaResponseDto> findByPropertyType(PropertyType propertyType);

    // Get specific rating criteria by property type and star level
    Optional<RatingCriteriaResponseDto> findByPropertyTypeAndStarLevel(PropertyType propertyType, Integer starLevel);

    // Get the maximum star level available for a property type
    Integer getMaxStarLevelForPropertyType(PropertyType propertyType);

    // Get the minimum star level available for a property type
    Integer getMinStarLevelForPropertyType(PropertyType propertyType);

    // Validate if a property type supports a specific star level
    boolean isStarLevelValidForPropertyType(PropertyType propertyType, Integer starLevel);
}
