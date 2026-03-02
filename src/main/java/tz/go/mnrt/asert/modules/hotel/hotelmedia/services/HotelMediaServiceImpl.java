package tz.go.mnrt.asert.modules.hotel.hotelmedia.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tz.go.mnrt.asert.modules.core.services.SimpleSearchService;
import tz.go.mnrt.asert.modules.hotel.hotel.entity.Hotel;
import tz.go.mnrt.asert.modules.hotel.hotel.repository.HotelRepository;
import tz.go.mnrt.asert.modules.hotel.hotelmedia.dtos.HotelMediaRequestDto;
import tz.go.mnrt.asert.modules.hotel.hotelmedia.dtos.HotelMediaResponseDto;
import tz.go.mnrt.asert.modules.hotel.hotelmedia.entity.HotelMedia;
import tz.go.mnrt.asert.modules.hotel.hotelmedia.repository.HotelMediaRepository;
import tz.go.mnrt.asert.modules.setup.fileupload.entity.FileUpload;
import tz.go.mnrt.asert.modules.setup.fileupload.repository.FileUploadRepository;
import tz.go.mnrt.asert.modules.user.entity.User;
import tz.go.mnrt.asert.modules.user.repository.UserRepository;

import javax.validation.ValidationException;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class HotelMediaServiceImpl extends SimpleSearchService<HotelMedia> implements HotelMediaService {

    private final HotelMediaRepository mediaRepository;
    private final HotelRepository hotelRepository;
    private final FileUploadRepository fileUploadRepository;
    private final UserRepository userRepository;

    @Value("${asert.uploaded-files.url}")
    private String uploadedFilesUrl;

    @Override
    @Transactional
    public HotelMediaRequestDto save(HotelMediaRequestDto mediaDto) {
        log.info("Saving hotel media: {}", mediaDto);

        // Find or create media
        HotelMedia media = new HotelMedia();
        if (mediaDto.getUuid() != null) {
            media = mediaRepository
                .findByUuid(mediaDto.getUuid())
                .orElseThrow(() -> new ValidationException(
                    "Hotel media with uuid {" + mediaDto.getUuid() + "} not found"));
        }

        // Find hotel
        Hotel hotel = hotelRepository
            .findById(mediaDto.getHotelId())
            .orElseThrow(() -> new ValidationException("Hotel with id {" + mediaDto.getHotelId() + "} not found"));

        // Find file upload
        FileUpload fileUpload = fileUploadRepository
            .findById(mediaDto.getMediaId())
            .orElseThrow(
                () -> new ValidationException("File upload with id {" + mediaDto.getMediaId() + "} not found"));

        // Find user (if provided)
        User user = null;
        if (mediaDto.getUserId() != null) {
            user = userRepository
                .findById(mediaDto.getUserId())
                .orElseThrow(
                    () -> new ValidationException("User with id {" + mediaDto.getUserId() + "} not found"));
        }

        // Copy properties from DTO to entity
        BeanUtils.copyProperties(mediaDto, media, "uuid", "hotel", "attachment", "user");

        // Set relationships
        media.setHotel(hotel);
        media.setAttachment(fileUpload);
        media.setUser(user);
        media.setMediaId(fileUpload.getId());
        media.setHotelId(hotel.getId());
        media.setUserId(user != null ? user.getId() : null);

        // Save media
        media = mediaRepository.save(media);

        // Set as default image if requested
        if (mediaDto.getIsDefault() != null && mediaDto.getIsDefault()) {
            hotel.setDefaultImage(media);
            hotelRepository.save(hotel);
        }

        // Update DTO with generated ID
        mediaDto.setId(media.getId());

        log.info("Hotel media saved successfully with ID: {}", media.getId());
        return mediaDto;
    }

    @Override
    public Page<HotelMediaResponseDto> findAll(Pageable page, Map<String, String> search) {
        log.info("Loading paginated hotel media with page {} and search {}", page, search);

        return mediaRepository
            .findAll(createSpecification(HotelMedia.class, search), page)
            .map(r -> new HotelMediaResponseDto(r, uploadedFilesUrl));
    }

    @Override
    public HotelMediaResponseDto findByUuid(UUID uuid) {
        log.info("Finding hotel media with uuid {}", uuid);

        return mediaRepository
            .findByUuid(uuid)
            .map(r -> new HotelMediaResponseDto(r, uploadedFilesUrl))
            .orElseThrow(() -> new ValidationException("Hotel media with uuid {" + uuid + "} not found"));
    }

    @Override
    public HotelMedia findEntityByUuid(UUID uuid) {
        log.info("Finding hotel media entity with uuid {}", uuid);

        return mediaRepository
            .findByUuid(uuid)
            .orElseThrow(() -> new ValidationException("Hotel media with uuid {" + uuid + "} not found"));
    }

    @Override
    @Transactional
    public void delete(UUID uuid) {
        log.info("Deleting hotel media with uuid {}", uuid);

        // Find media
        HotelMedia media = findEntityByUuid(uuid);

        // If this is a default image, remove it from hotel
        if (media.getIsDefault() && media.getHotel().getDefaultImage() != null &&
            media.getHotel().getDefaultImage().getId().equals(media.getId())) {
            Hotel hotel = media.getHotel();
            hotel.setDefaultImage(null);
            hotelRepository.save(hotel);
        }

        mediaRepository.softDelete(uuid);
    }

    @Override
    public Page<HotelMediaResponseDto> findAllByHotelUuid(UUID hotelUuid, Pageable page) {
        log.info("Finding all media for hotel with uuid {}", hotelUuid);

        return mediaRepository
            .findByHotelUuid(hotelUuid, page)
            .map(r -> new HotelMediaResponseDto(r, uploadedFilesUrl));
    }

    @Override
    @Transactional
    public HotelMediaResponseDto setAsDefaultImage(UUID hotelUuid, UUID mediaUuid) {
        log.info("Setting media {} as default for hotel {}", mediaUuid, hotelUuid);

        // Find hotel
        Hotel hotel = hotelRepository
            .findByUuid(hotelUuid)
            .orElseThrow(() -> new ValidationException("Hotel with uuid {" + hotelUuid + "} not found"));

        // Find media
        HotelMedia media = mediaRepository
            .findByUuid(mediaUuid)
            .orElseThrow(() -> new ValidationException("Hotel media with uuid {" + mediaUuid + "} not found"));

        // Verify media belongs to hotel
        if (!media.getHotel().getId().equals(hotel.getId())) {
            throw new ValidationException("Media with uuid {" + mediaUuid +
                "} does not belong to hotel with uuid {" + hotelUuid + "}");
        }

        // Set as default
        hotel.setDefaultImage(media);
        hotelRepository.save(hotel);

        return new HotelMediaResponseDto(media, uploadedFilesUrl);
    }
}
