package tz.go.mnrt.asert.modules.hotel.hotel.services;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageImpl;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.validation.ValidationException;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tz.go.mnrt.asert.modules.adminhierarchy.entity.AdminHierarchy;
import tz.go.mnrt.asert.modules.adminhierarchy.repository.AdminHierarchyRepository;
import tz.go.mnrt.asert.modules.assessment.assessor.dto.AssessorResponseDto;
import tz.go.mnrt.asert.modules.assessment.assessor.entity.Assessor;
import tz.go.mnrt.asert.modules.assessment.assessor.repository.AssessorRepository;
import tz.go.mnrt.asert.modules.assessment.hotelassessmentapproval.dtos.HotelAssessmentApprovalResponseDto;
import tz.go.mnrt.asert.modules.assessment.hotelassessmentapproval.repository.HotelAssessmentApprovalRepository;
import tz.go.mnrt.asert.modules.core.services.SimpleSearchService;
import tz.go.mnrt.asert.modules.form.form.repository.FormRepository;
import tz.go.mnrt.asert.modules.hotel.bedroomtype.entity.BedRoomType;
import tz.go.mnrt.asert.modules.hotel.bedroomtype.repository.BedRoomTypeRepository;
import tz.go.mnrt.asert.modules.hotel.bedtype.entity.BedType;
import tz.go.mnrt.asert.modules.hotel.bedtype.repository.BedTypeRepository;
import tz.go.mnrt.asert.modules.hotel.hotel.dtos.HotelAssessorsAssignmentRequestDto;
import tz.go.mnrt.asert.modules.hotel.hotel.dtos.HotelListingResponseDto;
import tz.go.mnrt.asert.modules.hotel.hotel.dtos.HotelMediaDto;
import tz.go.mnrt.asert.modules.hotel.hotel.dtos.HotelRequestDto;
import tz.go.mnrt.asert.modules.hotel.hotel.dtos.HotelResponseDto;
import tz.go.mnrt.asert.modules.hotel.hotel.dtos.HotelResponseMinDto;
import tz.go.mnrt.asert.modules.hotel.hotel.entity.Hotel;
import tz.go.mnrt.asert.modules.hotel.hotel.enums.HotelFacilityTypes;
import tz.go.mnrt.asert.modules.hotel.hotel.enums.PropertyType;
import tz.go.mnrt.asert.modules.hotel.hotel.repository.HotelRepository;
import tz.go.mnrt.asert.modules.hotel.hotel.state.HotelState;
import tz.go.mnrt.asert.modules.hotel.hotelfacility.dtos.HotelFacilityRequestDto;
import tz.go.mnrt.asert.modules.hotel.hotelfacility.entity.HotelFacility;
import tz.go.mnrt.asert.modules.hotel.hotelfacility.repository.HotelFacilityRepository;
import tz.go.mnrt.asert.modules.hotel.hotelmedia.entity.HotelMedia;
import tz.go.mnrt.asert.modules.hotel.hotelmedia.repository.HotelMediaRepository;
import tz.go.mnrt.asert.modules.hotel.roomtype.dtos.RoomTypeRequestDto;
import tz.go.mnrt.asert.modules.hotel.roomtype.entity.RoomType;
import tz.go.mnrt.asert.modules.hotel.roomtype.repository.RoomTypeRepository;
import tz.go.mnrt.asert.modules.role.entity.Role;
import tz.go.mnrt.asert.modules.setup.fileupload.entity.FileUpload;
import tz.go.mnrt.asert.modules.setup.fileupload.repository.FileUploadRepository;
import tz.go.mnrt.asert.modules.user.dtos.LoggedInUserDto;
import tz.go.mnrt.asert.modules.user.entity.User;
import tz.go.mnrt.asert.modules.user.repository.UserRepository;
import tz.go.mnrt.asert.modules.user.service.UserService;
import tz.go.mnrt.asert.modules.ratingcriteria.services.RatingCriteriaService;
import tz.go.mnrt.asert.modules.form.formsubmission.services.FormSubmissionService;
import tz.go.mnrt.asert.utils.DotEnvUtil;

@Service
@Slf4j
@RequiredArgsConstructor
public class HotelServiceImpl extends SimpleSearchService<Hotel> implements HotelService {
    private final HotelRepository hotelRepository;
    private final FormRepository formRepository;
    private final AssessorRepository assessorRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final HotelFacilityRepository hotelFacilityRepository;
    private final UserRepository userRepository;
    private final BedTypeRepository bedTypeRepository;
    private final BedRoomTypeRepository bedRoomTypeRepository;
    private final UserService userService;
    private final FileUploadRepository fileUploadRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final HotelMediaRepository hotelMediaRepository;
    private final EntityManager em;
    private final RatingCriteriaService ratingCriteriaService;
    private final FormSubmissionService formSubmissionService;
    private final HotelAssessmentApprovalRepository hotelAssessmentApprovalRepository;
    private final AdminHierarchyRepository adminHierarchyRepository;

    @Value("${asert.uploaded-files.url}")
    private String uploadedFilesUrl;

    @Override
    @Transactional
    public HotelRequestDto save(HotelRequestDto hotelRequestDto) {
        LoggedInUserDto logged = userService.loggedIn().orElse(null);
        if (logged == null) {
            log.error("User info not found");
            throw new ValidationException("User info not found");
        }

        User user = userRepository.findByUuid(logged.getUuid())
                .orElseThrow(() -> new ValidationException("User with uuid {" + logged.getUuid() + "} not found"));

        Hotel hotel = new Hotel();
        boolean isNewHotel = hotelRequestDto.getUuid() == null;

        if (!isNewHotel) {
            hotel = hotelRepository
                    .findByUuid(hotelRequestDto.getUuid())
                    .orElseThrow(
                            () -> new ValidationException(
                                    "Hotel with uuid {" + hotelRequestDto.getUuid() + "} not found"));
        }
        if (hotel.getIsActive() == null) {
            hotel.setIsActive(true);
        }

        if (hotelRequestDto.getLocationId() == null) {
            String message = String.format("Hotel Location ID given is: %d", hotelRequestDto.getLocationId());
            throw new ValidationException(message);
        }

        // Copy properties but exclude collections and relationships
        BeanUtils.copyProperties(hotelRequestDto, hotel, "uuid", "roomTypes", "facilities", "locationId");

        // get Admin Hierarchy from location id
        AdminHierarchy adminHierarchy = adminHierarchyRepository.findById(hotelRequestDto.getLocationId())
                .orElseThrow(() -> new ValidationException(
                        "AdminHierarchy with id {" + hotelRequestDto.getLocationId() + "} not found"));

        // set adminHierarchy
        hotel.setAdminHierarchy(adminHierarchy);

        // Set company and status
        hotel.setCompany(user.getCompany());

        hotel.setUserId(user.getId());

        // Only set status to DRAFT for new hotels
        if (isNewHotel) {
            hotel.setStatus(HotelState.DRAFT);
        }

        // Save the hotel first to get an ID
        hotel = hotelRepository.save(hotel);

        // Handle room types if they exist in the DTO
        if (hotelRequestDto.getRoomTypes() != null && !hotelRequestDto.getRoomTypes().isEmpty()) {
            // If updating, manage existing room types
            if (!isNewHotel) {
                // Get existing room types
                List<RoomType> existingRoomTypes = roomTypeRepository.findByHotelId(hotel.getId());
                Map<UUID, RoomType> existingRoomTypeMap = existingRoomTypes.stream()
                        .filter(rt -> rt.getUuid() != null)
                        .collect(Collectors.toMap(RoomType::getUuid, rt -> rt));

                // Track which room types to keep
                Set<UUID> processedRoomTypeUuids = new HashSet<>();

                // Process each room type in the DTO
                for (RoomTypeRequestDto roomTypeDto : hotelRequestDto.getRoomTypes()) {
                    RoomType roomType;

                    BedType bedType = bedTypeRepository.findById(roomTypeDto.getBedTypeId())
                            .orElseThrow(() -> new ValidationException(
                                    "BedType with id {" + roomTypeDto.getBedTypeId() + "} not found"));

                    BedRoomType bedRoomType = bedRoomTypeRepository.findById(roomTypeDto.getBedRoomTypeId())
                            .orElseThrow(() -> new ValidationException(
                                    "BedRoomType with id {" + roomTypeDto.getBedTypeId() + "} not found"));

                    // If room type has UUID, try to find it in existing room types
                    if (roomTypeDto.getUuid() != null
                            && existingRoomTypeMap.containsKey((roomTypeDto.getUuid()))) {
                        roomType = existingRoomTypeMap.get((roomTypeDto.getUuid()));
                        processedRoomTypeUuids.add(roomType.getUuid());
                    } else {
                        roomType = new RoomType();
                    }

                    // Copy properties from DTO to entity (excluding amenities)
                    BeanUtils.copyProperties(roomTypeDto, roomType, "uuid", "amenities", "hotel");

                    // Set hotel reference
                    roomType.setHotel(hotel);
                    roomType.setName(bedType.getName());
                    roomType.setType(bedType);
                    roomType.setBedRoomType(bedRoomType);

                    // Save or update amenities
                    if (roomTypeDto.getAmenities() != null) {
                        // Clear existing amenities and add new ones
                        roomType.setAmenities(new HashSet<>(roomTypeDto.getAmenities()));
                    }

                    // Save room type
                    roomTypeRepository.save(roomType);
                }

                // Delete room types that weren't in the update
                existingRoomTypes.stream()
                        .filter(rt -> rt.getUuid() != null && !processedRoomTypeUuids.contains(rt.getUuid()))
                        .forEach(roomTypeRepository::delete);

            } else {
                // For new hotels, create all room types
                for (RoomTypeRequestDto roomTypeDto : hotelRequestDto.getRoomTypes()) {
                    RoomType roomType = new RoomType();

                    BedType bedType = bedTypeRepository.findById(roomTypeDto.getBedTypeId())
                            .orElseThrow(() -> new ValidationException(
                                    "BedType with id {" + roomTypeDto.getBedTypeId() + "} not found"));

                    BedRoomType bedRoomType = bedRoomTypeRepository.findById(roomTypeDto.getBedRoomTypeId())
                            .orElseThrow(() -> new ValidationException(
                                    "BedRoomType with id {" + roomTypeDto.getBedTypeId() + "} not found"));
                    // Copy properties from DTO to entity (excluding amenities)
                    BeanUtils.copyProperties(roomTypeDto, roomType, "uuid", "amenities", "hotel");

                    // Set hotel reference
                    roomType.setHotel(hotel);
                    roomType.setType(bedType);
                    roomType.setBedRoomType(bedRoomType);
                    roomType.setName(bedType.getName());

                    // Set amenities
                    if (roomTypeDto.getAmenities() != null) {
                        roomType.setAmenities(new HashSet<>(roomTypeDto.getAmenities()));
                    } else {
                        roomType.setAmenities(new HashSet<>());
                    }

                    // Save room type
                    roomTypeRepository.save(roomType);
                }
            }
        }

        // Handle facilities if they exist in the DTO
        if (hotelRequestDto.getFacilities() != null && !hotelRequestDto.getFacilities().isEmpty()) {
            // If updating, manage existing facilities
            if (!isNewHotel) {
                // Get existing facilities
                List<HotelFacility> existingFacilities = hotelFacilityRepository.findByHotelId(hotel.getId());
                Map<UUID, HotelFacility> existingFacilityMap = existingFacilities.stream()
                        .filter(f -> f.getUuid() != null)
                        .collect(Collectors.toMap(HotelFacility::getUuid, f -> f));

                // Track which facilities to keep
                Set<UUID> processedFacilityUuids = new HashSet<>();

                // Process each facility in the DTO
                for (HotelFacilityRequestDto facilityDto : hotelRequestDto.getFacilities()) {
                    HotelFacility facility;

                    // If facility has UUID, try to find it in existing facilities
                    if (facilityDto.getUuid() != null
                            && existingFacilityMap.containsKey(facilityDto.getUuid())) {
                        facility = existingFacilityMap.get(facilityDto.getUuid());
                        processedFacilityUuids.add(facility.getUuid());
                    } else {
                        facility = new HotelFacility();
                    }

                    // Copy properties from DTO to entity
                    BeanUtils.copyProperties(facilityDto, facility, "uuid", "hotel");

                    // Set hotel reference
                    facility.setHotel(hotel);

                    // Save facility
                    hotelFacilityRepository.save(facility);
                }

                // Delete facilities that weren't in the update
                existingFacilities.stream()
                        .filter(f -> f.getUuid() != null && !processedFacilityUuids.contains(f.getUuid()))
                        .forEach(hotelFacilityRepository::delete);

            } else {
                // For new hotels, create all facilities
                for (HotelFacilityRequestDto facilityDto : hotelRequestDto.getFacilities()) {
                    HotelFacility facility = new HotelFacility();

                    // Copy properties from DTO to entity
                    BeanUtils.copyProperties(facilityDto, facility, "uuid", "hotel");

                    // Set hotel reference
                    facility.setHotel(hotel);

                    // Save facility
                    hotelFacilityRepository.save(facility);
                }
            }
        }

        // Update the DTO with the saved hotel's ID

        if (hotelRequestDto.getHotelMediaDtoList() != null && !hotelRequestDto.getHotelMediaDtoList().isEmpty()) {
            try {
                boolean isFirstImage = true;
                Long defaultImageId = null;

                for (HotelMediaDto mediaDto : hotelRequestDto.getHotelMediaDtoList()) {
                    // Use existing FileUpload record referenced by attachmentId
                    FileUpload upload = null;

                    if (mediaDto.getAttachmentId() != null) {
                        // Reference existing uploaded file
                        upload = fileUploadRepository.findById(mediaDto.getAttachmentId())
                                .orElseThrow(() -> new ValidationException(
                                        "FileUpload with id {" + mediaDto.getAttachmentId() + "} not found"));
                    } else {
                        // Fallback: Create new FileUpload for base64 (legacy support)
                        upload = new FileUpload();
                        // upload.setFilePath(baseSixtyFourService.saveFile(mediaDto.getBase64(),
                        // mediaDto.getName()));
                        upload.setFileSize(mediaDto.getFileSize());
                        upload.setFileType("base64");
                        upload.setUploadType("Attachment");
                        upload.setName(mediaDto.getName());
                        upload.setUserId(user.getId());
                        upload = fileUploadRepository.save(upload);
                    }

                    HotelMedia hotelMedia = new HotelMedia();
                    BeanUtils.copyProperties(mediaDto, hotelMedia);
                    hotelMedia.setUuid(UUID.randomUUID());
                    hotelMedia.setIsApproved(true);
                    // Only set first image as default
                    hotelMedia.setIsDefault(isFirstImage);
                    hotelMedia.setMediaId(upload.getId());
                    hotelMedia.setDescription(mediaDto.getName());
                    hotelMedia.setUserId(user.getId());
                    hotelMedia.setHotelId(hotel.getId());
                    HotelMedia row = hotelMediaRepository.save(hotelMedia);

                    // Set the first image as the hotel's default image
                    if (isFirstImage) {
                        defaultImageId = row.getId();
                        isFirstImage = false;
                    }
                }

                // Update hotel with default image ID
                if (defaultImageId != null) {
                    hotel.setDefaultImageId(defaultImageId);
                    hotelRepository.save(hotel);
                }
            } catch (Exception e) {
                log.error("We have an error saving attachment: {}", e.getMessage());
                System.out.println(e.getMessage());
                throw new RuntimeException(e);
            }
        }

        hotelRequestDto.setId(hotel.getId());

        return hotelRequestDto;
    }

    @Override
    public Page<HotelResponseMinDto> newAssignedHotels(Pageable page, Map<String, String> search) {
        return hotelRepository
                .findAll(createSpecification(Hotel.class, search), page)
                .map(HotelResponseMinDto::new);
    }

    @Override
    public Page<HotelResponseMinDto> newUnAssignedHotels(Pageable page, Map<String, String> search) {
        return hotelRepository
                .findAll(createSpecification(Hotel.class, search), page)
                .map(HotelResponseMinDto::new);
    }

    @Override
    public Page<HotelListingResponseDto> findPublicListing(Pageable page, Map<String, String> search) {
        // Create a copy of the search map to avoid modifying the original
        Map<String, String> processedSearch = new HashMap<>(search);

        // Extract special filters that need custom handling
        String minRatingValue = processedSearch.remove("minRating");
        String amenitiesValue = processedSearch.remove("amenities");
        String propertyTypeValue = processedSearch.remove("propertyType");

        // Create base specification for regular fields
        Specification<Hotel> specs = createSpecification(Hotel.class, processedSearch);

        // Add amenities filtering if specified
        if (amenitiesValue != null && !amenitiesValue.trim().isEmpty()) {
            specs = specs.and(createAmenitiesSpecification(amenitiesValue));
        }

        // Add enum-based filters with exact matching
        if (propertyTypeValue != null) {
            try {
                PropertyType propertyType = PropertyType.valueOf(propertyTypeValue);
                log.info("Filtering hotels by propertyType: {}", propertyType);
                specs = specs.and((root, query, cb) -> cb.equal(root.get("propertyType"), propertyType));
            } catch (IllegalArgumentException e) {
                // Skip this filter if invalid value provided
                log.warn("Invalid propertyType value: {}", propertyTypeValue);
            }
        }

        // Get the results with regular filtering and rating fields populated
        Page<HotelListingResponseDto> results = hotelRepository
                .findAll(specs, page)
                .map(hotel -> {
                    HotelListingResponseDto dto = new HotelListingResponseDto(hotel, uploadedFilesUrl);
                    setHotelRatingFields(dto, hotel.getUuid());
                    return dto;
                });

        // Apply post-processing rating filter if needed
        if (minRatingValue != null && !minRatingValue.trim().isEmpty()) {
            results = filterByMinRating(results, minRatingValue);
        }

        return results;
    }

    @Override
    public Page<HotelResponseDto> findAll(Pageable page, Map<String, String> search) {
        log.info("Loading paginated Hotels with page {} and search {}", page, search);

        // Create a copy of the search map to avoid modifying the original
        Map<String, String> processedSearch = new HashMap<>(search);

        // Handle text fields with LIKE queries
        if (processedSearch.containsKey("name")) {
            processedSearch.put("name", "%" + processedSearch.get("name") + "%");
        }

        // Handle enum fields - remove them from the standard search
        // We'll handle them separately with exact matching
        String propertyTypeValue = null;
        if (processedSearch.containsKey("propertyType")) {
            propertyTypeValue = processedSearch.get("propertyType");
            processedSearch.remove("propertyType");
        }

        String statusValue = null;
        if (processedSearch.containsKey("status")) {
            statusValue = processedSearch.get("status");
            processedSearch.remove("status");
        }

        // Authorization logic
        LoggedInUserDto logged = userService.loggedIn().orElse(null);

        if (logged == null) {
            log.error("User info not found");
            throw new ValidationException("User info not found");
        }

        User user = userRepository.findByUuid(logged.getUuid())
                .orElseThrow(() -> new ValidationException("User with uuid {" + logged.getUuid() + "} not found"));

        log.info("Found user with id: {} and email {}", user.getId(), user.getEmail());

        Set<Role> userRoles = user.getRoles();
        boolean isClientRole = userRoles.stream()
                .filter(Objects::nonNull)
                .anyMatch(role -> {
                    Boolean isClient = role.getIsClient();
                    return isClient != null && isClient;
                });

        boolean isAssessor = userRoles.stream()
                .anyMatch(role -> role.getCode().equalsIgnoreCase("ASSESSOR"));

        // Start building specifications
        Specification<Hotel> specs = Specification.where(null);

        // Add user role-based filters
        if (isClientRole) {
            specs = specs.and((root, query, cb) -> cb.equal(root.get("userId"), user.getId()));
        } else if (isAssessor) {
            // Get assessor based on user id
            Assessor assessor = assessorRepository.findByUserId(user.getId())
                    .orElseThrow(
                            () -> new ValidationException("Assessor with user id {" + user.getId() + "} not found"));

            List<Long> hotelIds = assessor.getHotels()
                    .stream()
                    .map(Hotel::getId)
                    .collect(Collectors.toList());

            if (hotelIds.isEmpty()) {
                // If no hotels are assigned, return empty set
                return Page.empty(page);
            }

            specs = specs.and(((root, query, cb) -> root.get("id").in(hotelIds)));
        } else {
            HotelState hotelState = HotelState.SUBMITED_FOR_GRADING;
            specs = specs.and((root, query, cb) -> cb.equal(root.get("status"), hotelState));
        }

        // Add enum-based filters with exact matching
        if (propertyTypeValue != null) {
            try {
                PropertyType propertyType = PropertyType.valueOf(propertyTypeValue);
                specs = specs.and((root, query, cb) -> cb.equal(root.get("propertyType"), propertyType));
            } catch (IllegalArgumentException e) {
                log.warn("Invalid propertyType value: {}", propertyTypeValue);
                // Skip this filter if invalid value provided
            }
        }

        if (statusValue != null) {
            try {
                HotelState status = HotelState.valueOf(statusValue);
                specs = specs.and((root, query, cb) -> cb.equal(root.get("status"), status));
            } catch (IllegalArgumentException e) {
                log.warn("Invalid status value: {}", statusValue);
                // Skip this filter if invalid value provided
            }
        }

        specs = specs.and(createSpecification(Hotel.class, processedSearch));

        // Execute the query with all specifications combined
        return hotelRepository
                .findAll(specs, page)
                .map(hotel -> {
                    HotelResponseDto hotelResponseDto = new HotelResponseDto(hotel, uploadedFilesUrl);

                    // Find and set the associated form based on hotel property type
                    if (hotel.getPropertyType() != null) {
                        formRepository.findByPropertyTypesContainingAndIsDeletedFalse(hotel.getPropertyType().name())
                                .ifPresent(form -> {
                                    hotelResponseDto.setFormUuid(form.getUuid());
                                    hotelResponseDto.setFormName(form.getName());
                                });
                    }

                    return hotelResponseDto;
                });
    }

    @Override
    public HotelResponseDto findByUuid(UUID uuid) {
        log.info("finding hotel with uuid {} ", uuid);
        return hotelRepository
                .findByUuid(uuid)
                .map(hotel -> {
                    HotelResponseDto hotelResponseDto = new HotelResponseDto(hotel, uploadedFilesUrl);

                    // Find and set the associated form based on hotel property type
                    if (hotel.getPropertyType() != null) {
                        formRepository.findByPropertyTypesContainingAndIsDeletedFalse(hotel.getPropertyType().name())
                                .ifPresent(form -> {
                                    hotelResponseDto.setFormUuid(form.getUuid());
                                    hotelResponseDto.setFormName(form.getName());
                                });
                    }

                    return hotelResponseDto;
                })
                .orElseThrow(() -> new ValidationException("Hotel with uuid {" + uuid + "} not found"));
    }

    @Override
    public void delete(UUID uuid) {
        log.info("deleting Hotel with uuid {} ", uuid);
        hotelRepository.softDelete(uuid);
    }

    @Override
    public Hotel findEntityByUuid(UUID uuid) {
        log.info("Finding hotel entity with uuid {} ", uuid);
        return hotelRepository
                .findByUuid(uuid)
                .orElseThrow(() -> new ValidationException("Hotel with uuid {" + uuid + "} not found"));
    }

    public Optional<Hotel> findById(Long id) {
        return hotelRepository.findById(id);
    }

    public Optional<Hotel> findByuid(UUID uuid) {
        return hotelRepository.findByUuid(uuid);
    }

    @Override
    public List<PropertyType> getAllTypes() {
        return Arrays.asList(PropertyType.values());
    }

    @Override
    public List<?> importQuery(String tin) {
        try {

            String url = DotEnvUtil.getEnvVariable("PORTAL_FACILITY_API");
            ;
            Map<String, String> params = new HashMap<>();
            params.put("tin", tin);
            ResponseEntity<String> response = this.restTemplate.getForEntity(url, String.class, params);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            List<Map<String, Object>> establishments = mapper.convertValue(
                    root.get("data").get("establishments"),
                    new com.fasterxml.jackson.core.type.TypeReference<List<Map<String, Object>>>() {
                    });
            return establishments;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ValidationException("Request failed");
        }
    }

    @Transactional
    public HotelResponseDto assignAssessorsToHotel(UUID hotelUuid, List<UUID> assessorUuids) {
        log.info("Assigning assessors {} to hotel {}", assessorUuids, hotelUuid);

        Hotel hotel = hotelRepository.findByUuid(hotelUuid)
                .orElseThrow(() -> new ValidationException("Hotel with UUID " + hotelUuid + " not found"));

        // Initialize assessors set if null
        if (hotel.getAssessors() == null) {
            hotel.setAssessors(new HashSet<>());
        }

        // Handle the incoming assessor list as a "set" operation (final desired state)
        if (assessorUuids != null) {
            // Remove duplicates from incoming list
            Set<UUID> uniqueIncomingUuids = new HashSet<>(assessorUuids);

            // Validate that the final count doesn't exceed 3
            if (uniqueIncomingUuids.size() > 3) {
                throw new ValidationException("Cannot assign more than 3 assessors to a hotel. Requested: "
                        + uniqueIncomingUuids.size());
            }

            // Fetch all requested assessors first to validate they exist
            Set<Assessor> newAssessors = new HashSet<>();
            for (UUID assessorUuid : uniqueIncomingUuids) {
                Assessor assessor = assessorRepository.findByUuid(assessorUuid)
                        .orElseThrow(
                                () -> new ValidationException("Assessor with UUID " + assessorUuid + " not found"));
                newAssessors.add(assessor);
            }

            // Replace the entire assessor set with the new set (handles add/remove in one
            // operation)
            hotel.getAssessors().clear();
            hotel.getAssessors().addAll(newAssessors);
        } else {
            // If null list provided, clear all assessors
            hotel.getAssessors().clear();
        }

        // Save the updated hotel
        hotel = hotelRepository.save(hotel);

        // Return the updated hotel response
        HotelResponseDto response = new HotelResponseDto(hotel, uploadedFilesUrl);

        // Find and set the form for this hotel
        if (hotel.getPropertyType() != null) {
            formRepository.findByPropertyTypesContainingAndIsDeletedFalse(hotel.getPropertyType().name())
                    .ifPresent(form -> {
                        response.setFormUuid(form.getUuid());
                        response.setFormName(form.getName());
                    });
        }

        return response;
    }

    @Transactional
    @Override
    public HotelResponseDto removeAssessorsFromHotel(UUID hotelUuid, List<UUID> assessorUuids) {
        log.info("Removing assessors {} from hotel {}", assessorUuids, hotelUuid);

        Hotel hotel = hotelRepository.findByUuid(hotelUuid)
                .orElseThrow(() -> new ValidationException("Hotel with UUID " + hotelUuid + " not found"));

        if (assessorUuids != null && !assessorUuids.isEmpty()) {
            for (UUID assessorUuid : assessorUuids) {
                Assessor assessor = assessorRepository.findByUuid(assessorUuid)
                        .orElseThrow(
                                () -> new ValidationException("Assessor with UUID " + assessorUuid + " not found"));

                // Remove the assessor from the hotel
                hotel.getAssessors().remove(assessor);
            }
        }

        // Save the updated hotel
        hotel = hotelRepository.save(hotel);

        // Return the updated hotel response
        HotelResponseDto response = new HotelResponseDto(hotel, uploadedFilesUrl);

        // Find and set the form for this hotel
        if (hotel.getPropertyType() != null) {
            formRepository.findByPropertyTypesContainingAndIsDeletedFalse(hotel.getPropertyType().name())
                    .ifPresent(form -> {
                        response.setFormUuid(form.getUuid());
                        response.setFormName(form.getName());
                    });
        }

        return response;
    }

    @Override
    public List<AssessorResponseDto> getHotelAssessors(UUID hotelUuid) {
        log.info("Getting assessors for hotel {}", hotelUuid);

        Hotel hotel = hotelRepository.findByUuid(hotelUuid)
                .orElseThrow(() -> new ValidationException("Hotel with UUID " + hotelUuid + " not found"));

        return hotel.getAssessors().stream()
                .map(assessor -> new AssessorResponseDto(assessor))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public HotelResponseDto requestAssessment(UUID uuid) {
        Hotel hotel = hotelRepository.findByUuid(uuid)
                .orElseThrow(() -> new ValidationException("Hotel with UUID " + uuid + " not found"));

        System.out.println("current hotel status: " + hotel.getStatus());
        hotel.setStatus(HotelState.SUBMITED_FOR_GRADING);

        // Save the hotel directly to update the status
        hotelRepository.save(hotel);

        // Return a minimal HotelResponseDto
        HotelResponseDto responseDto = new HotelResponseDto();
        responseDto.setUuid(hotel.getUuid());
        responseDto.setStatus(hotel.getStatus());

        return responseDto;
    }

    @Override
    @Transactional
    public void assignAssessorsToHotel(UUID hotelUuid, HotelAssessorsAssignmentRequestDto dto) {
        List<Long> requestedAssessorIds = dto.getAssessorIds();
        log.info("Assigning assessors {} to hotel {}", requestedAssessorIds, hotelUuid);

        // Fetch the hotel and throw an exception if not found.
        Hotel hotel = hotelRepository.findByUuid(hotelUuid)
                .orElseThrow(() -> new ValidationException("Hotel with UUID " + hotelUuid + " not found"));

        // Initialize assessors set if null
        if (hotel.getAssessors() == null) {
            hotel.setAssessors(new HashSet<>());
        }

        // Handle the incoming assessor list as a "set" operation (final desired state)
        if (requestedAssessorIds != null) {
            // Remove duplicates from incoming list
            Set<Long> uniqueIncomingIds = new HashSet<>(requestedAssessorIds);

            // Validate that the final count doesn't exceed 3
            if (uniqueIncomingIds.size() > 3) {
                throw new ValidationException("Cannot assign more than 3 assessors to a hotel. Requested: "
                        + uniqueIncomingIds.size());
            }

            // Fetch all requested assessors first to validate they exist
            Set<Assessor> newAssessors = new HashSet<>();
            for (Long assessorId : uniqueIncomingIds) {
                Assessor assessor = assessorRepository.findById(assessorId)
                        .orElseThrow(
                                () -> new ValidationException("Assessor with ID " + assessorId + " not found"));
                newAssessors.add(assessor);
            }

            // Replace the entire assessor set with the new set (handles add/remove in one
            // operation)
            hotel.getAssessors().clear();
            hotel.getAssessors().addAll(newAssessors);
        } else {
            // If null list provided, clear all assessors
            hotel.getAssessors().clear();
        }

        // Save the updated hotel entity.
        hotelRepository.save(hotel);
    }

    @Override
    public HotelResponseDto findPublicFacilityByUuid(UUID uuid) {
        log.info("finding hotel with uuid {} ", uuid);
        return hotelRepository
                .findByUuid(uuid)
                .map(hotel -> {
                    HotelResponseDto dto = new HotelResponseDto(hotel, uploadedFilesUrl);
                    setHotelRatingFields(dto, hotel.getUuid());
                    return dto;
                })
                .orElseThrow(() -> new ValidationException("Hotel with uuid {" + uuid + "} not found"));
    }

    private void setHotelRatingFields(HotelListingResponseDto dto, UUID hotelUuid) {
        try {
            var assessmentResult = formSubmissionService.getHotelAssessmentResult(hotelUuid);
            dto.setAggregateScore(assessmentResult.getAggregateScore());
            dto.setStarRating(assessmentResult.getStarRating());
        } catch (Exception e) {
            log.debug("No assessment data found for hotel {}: {}", hotelUuid, e.getMessage());
            dto.setAggregateScore(null);
            dto.setStarRating(null);
        }
    }

    private void setHotelRatingFields(HotelResponseDto dto, UUID hotelUuid) {
        try {
            var assessmentResult = formSubmissionService.getHotelAssessmentResult(hotelUuid);
            dto.setAggregateScore(assessmentResult.getAggregateScore());
            dto.setStarRating(assessmentResult.getStarRating());
        } catch (Exception e) {
            log.debug("No assessment data found for hotel {}: {}", hotelUuid, e.getMessage());
            dto.setAggregateScore(null);
            dto.setStarRating(null);
        }
    }

    private Specification<Hotel> createAmenitiesSpecification(String amenitiesValue) {
        return (root, query, criteriaBuilder) -> {
            String[] amenities = amenitiesValue.split(",");

            // Add DISTINCT to prevent duplicates when hotel has multiple matching amenities
            query.distinct(true);

            // Join with hotel facilities
            var facilitiesJoin = root.join("facilities");

            // Create OR conditions for each amenity
            javax.persistence.criteria.Predicate[] amenityPredicates = Arrays.stream(amenities)
                    .map(String::trim)
                    .filter(amenity -> !amenity.isEmpty())
                    .map(amenity -> {
                        try {
                            // Convert string to enum value
                            HotelFacilityTypes facilityType = HotelFacilityTypes.valueOf(amenity.toUpperCase());
                            return criteriaBuilder.equal(facilitiesJoin.get("facilityType"), facilityType);
                        } catch (IllegalArgumentException e) {
                            // Log invalid amenity values and skip them
                            log.warn("Invalid amenity value: {}", amenity);
                            return criteriaBuilder.conjunction(); // Always true condition (no-op)
                        }
                    })
                    .toArray(javax.persistence.criteria.Predicate[]::new);

            return criteriaBuilder.or(amenityPredicates);
        };
    }

    private Page<HotelListingResponseDto> filterByMinRating(Page<HotelListingResponseDto> results,
            String minRatingValue) {
        try {
            int minRating = Integer.parseInt(minRatingValue);

            List<HotelListingResponseDto> filteredContent = results.getContent().stream()
                    .filter(hotel -> {
                        String starRating = hotel.getStarRating();
                        if (starRating == null || starRating.isEmpty()) {
                            return false; // Ungraded facilities don't meet minimum rating
                        }

                        // Extract numeric rating from string like "3 Star" or "4 Star"
                        try {
                            String[] parts = starRating.split(" ");
                            if (parts.length > 0) {
                                int hotelRating = Integer.parseInt(parts[0]);
                                return hotelRating >= minRating;
                            }
                        } catch (NumberFormatException e) {
                            log.debug("Could not parse rating for hotel: {}", starRating);
                        }

                        return false;
                    })
                    .collect(Collectors.toList());

            return new PageImpl<>(filteredContent, results.getPageable(), filteredContent.size());
        } catch (NumberFormatException e) {
            log.warn("Invalid minRating value: {}", minRatingValue);
            return results; // Return unfiltered results if invalid rating
        }
    }
}
