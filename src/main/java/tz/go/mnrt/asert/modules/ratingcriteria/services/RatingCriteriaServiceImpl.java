package tz.go.mnrt.asert.modules.ratingcriteria.services;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.ValidationException;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tz.go.mnrt.asert.modules.core.services.SimpleSearchService;
import tz.go.mnrt.asert.modules.hotel.hotel.enums.PropertyType;
import tz.go.mnrt.asert.modules.ratingcriteria.dtos.RatingCriteriaRequestDto;
import tz.go.mnrt.asert.modules.ratingcriteria.dtos.RatingCriteriaResponseDto;
import tz.go.mnrt.asert.modules.ratingcriteria.entity.RatingCriteria;
import tz.go.mnrt.asert.modules.ratingcriteria.repository.RatingCriteriaRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class RatingCriteriaServiceImpl extends SimpleSearchService<RatingCriteria> implements RatingCriteriaService {
    private final RatingCriteriaRepository ratingCriteriaRepository;

    @Override
    public RatingCriteriaRequestDto save(RatingCriteriaRequestDto ratingCriteriaRequestDto) {
        RatingCriteria ratingCriteria = new RatingCriteria();
        if (ratingCriteriaRequestDto.getUuid() != null) {
            ratingCriteria = ratingCriteriaRepository
                    .findByUuid(ratingCriteriaRequestDto.getUuid())
                    .orElseThrow(
                            () -> new ValidationException(
                                    "RatingCriteria with uuid {" + ratingCriteriaRequestDto.getUuid() + "} not found"));
        }
        BeanUtils.copyProperties(ratingCriteriaRequestDto, ratingCriteria, "uuid");
        assert (ratingCriteria.getUuid() != null);
        ratingCriteria = ratingCriteriaRepository.save(ratingCriteria);
        ratingCriteriaRequestDto.setId(ratingCriteria.getId());
        return ratingCriteriaRequestDto;
    }

    @Override
    public Page<RatingCriteriaResponseDto> findAll(Pageable page, Map<String, String> search) {
        log.info("Loading paginated RatingCriterias with page {} and search {} ", page, search);

        // Handle propertyType separately since it's an enum that needs exact matching
        PropertyType propertyTypeFilter = null;
        if (search.containsKey("propertyType") && search.get("propertyType") != null
                && !search.get("propertyType").isEmpty()) {
            try {
                propertyTypeFilter = PropertyType.valueOf(search.get("propertyType"));
                search.remove("propertyType"); // Remove it from search to avoid LIKE queries
            } catch (IllegalArgumentException e) {
                log.warn("Invalid property type value: {}", search.get("propertyType"));
            }
        }

        // Create specification for remaining search parameters
        Specification<RatingCriteria> spec = createSpecification(RatingCriteria.class, search);

        // Add propertyType filter if provided
        if (propertyTypeFilter != null) {
            final PropertyType finalPropertyType = propertyTypeFilter;
            Specification<RatingCriteria> propertyTypeSpec = (root, query, criteriaBuilder) -> criteriaBuilder
                    .equal(root.get("propertyType"), finalPropertyType);

            spec = spec != null ? spec.and(propertyTypeSpec) : propertyTypeSpec;
        }

        return ratingCriteriaRepository
                .findAll(spec, page)
                .map(RatingCriteriaResponseDto::new);
    }

    @Override
    public RatingCriteriaResponseDto findByUuid(UUID uuid) {
        log.info("finding role with uuid {} ", uuid);
        return ratingCriteriaRepository
                .findByUuid(uuid)
                .map(RatingCriteriaResponseDto::new)
                .orElseThrow(() -> new ValidationException("RatingCriteria with uuid {" + uuid + "} not found"));
    }

    @Override
    public void delete(UUID uuid) {
        log.info("deleting RatingCriteria with uuid {} ", uuid);
        ratingCriteriaRepository.softDelete(uuid);
    }

    @Override
    @Deprecated
    public String getStarRatingByScore(Double score) {
        log.warn(
                "Using deprecated method getStarRatingByScore. Consider using getStarRatingByScoreAndPropertyType instead.");
        if (score == null) {
            return null;
        }

        return ratingCriteriaRepository.findAll()
                .stream()
                .filter(criteria -> !criteria.isDeleted())
                .filter(criteria -> score >= criteria.getMinScore() && score <= criteria.getMaxScore())
                .findFirst()
                .map(criteria -> criteria.getStarLevel() + " Star")
                .orElse(null);
    }

    @Override
    public String getStarRatingByScoreAndPropertyType(Double score, PropertyType propertyType) {
        log.info("Getting star rating for score {} and property type {}", score, propertyType);

        if (score == null || propertyType == null) {
            return null;
        }

        return ratingCriteriaRepository.findByPropertyTypeAndScoreRange(propertyType, score)
                .map(criteria -> criteria.getStarLevel() + " Star")
                .orElse(null);
    }

    @Override
    public List<RatingCriteriaResponseDto> findByPropertyType(PropertyType propertyType) {
        log.info("Finding rating criteria for property type: {}", propertyType);

        if (propertyType == null) {
            throw new ValidationException("Property type cannot be null");
        }

        return ratingCriteriaRepository.findByPropertyTypeAndIsDeletedFalseOrderByStarLevelAsc(propertyType)
                .stream()
                .map(RatingCriteriaResponseDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<RatingCriteriaResponseDto> findByPropertyTypeAndStarLevel(PropertyType propertyType,
            Integer starLevel) {
        log.info("Finding rating criteria for property type {} and star level {}", propertyType, starLevel);

        if (propertyType == null || starLevel == null) {
            return Optional.empty();
        }

        return ratingCriteriaRepository.findByPropertyTypeAndStarLevelAndIsDeletedFalse(propertyType, starLevel)
                .map(RatingCriteriaResponseDto::new);
    }

    @Override
    public Integer getMaxStarLevelForPropertyType(PropertyType propertyType) {
        log.info("Getting max star level for property type: {}", propertyType);

        if (propertyType == null) {
            return null;
        }

        return ratingCriteriaRepository.findMaxStarLevelByPropertyType(propertyType)
                .orElse(null);
    }

    @Override
    public Integer getMinStarLevelForPropertyType(PropertyType propertyType) {
        log.info("Getting min star level for property type: {}", propertyType);

        if (propertyType == null) {
            return null;
        }

        return ratingCriteriaRepository.findMinStarLevelByPropertyType(propertyType)
                .orElse(null);
    }

    @Override
    public boolean isStarLevelValidForPropertyType(PropertyType propertyType, Integer starLevel) {
        log.info("Validating star level {} for property type {}", starLevel, propertyType);

        if (propertyType == null || starLevel == null) {
            return false;
        }

        return ratingCriteriaRepository.existsByPropertyTypeAndStarLevelAndIsDeletedFalse(propertyType, starLevel);
    }

    @Override
    public String getTopStarRatingByPropertyTypeAndScore(Double score, PropertyType propertyType) {
        return ratingCriteriaRepository
                .findTopByPropertyTypeAndScore(propertyType, score)
                .stream()
                .findFirst()
                .map(rc -> rc.getStarLevel() + " Star")
                .orElse(null);
    }
}
