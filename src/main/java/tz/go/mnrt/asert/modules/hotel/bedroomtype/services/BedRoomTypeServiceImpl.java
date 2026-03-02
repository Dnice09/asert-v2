package tz.go.mnrt.asert.modules.hotel.bedroomtype.services;

import java.util.Map;
import java.util.UUID;

import javax.validation.ValidationException;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tz.go.mnrt.asert.modules.core.services.SimpleSearchService;
import tz.go.mnrt.asert.modules.hotel.bedroomtype.dtos.BedRoomTypeRequestDto;
import tz.go.mnrt.asert.modules.hotel.bedroomtype.dtos.BedRoomTypeResponseDto;
import tz.go.mnrt.asert.modules.hotel.bedroomtype.entity.BedRoomType;
import tz.go.mnrt.asert.modules.hotel.bedroomtype.repository.BedRoomTypeRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class BedRoomTypeServiceImpl extends SimpleSearchService<BedRoomType> implements BedRoomTypeService {
    private final BedRoomTypeRepository bedRoomTypeRepository;

    @Override
    public BedRoomTypeRequestDto save(BedRoomTypeRequestDto bedTypeRequestDto) {
        BedRoomType bedType = new BedRoomType();
        if (bedTypeRequestDto.getUuid() != null) {
            bedType = bedRoomTypeRepository
                    .findByUuid(bedTypeRequestDto.getUuid())
                    .orElseThrow(
                            () -> new ValidationException(
                                    "BedRoomType with uuid {" + bedTypeRequestDto.getUuid() + "} not found"));
        }
        BeanUtils.copyProperties(bedTypeRequestDto, bedType, "uuid");
        assert (bedType.getUuid() != null);
        bedType = bedRoomTypeRepository.save(bedType);
        bedTypeRequestDto.setId(bedType.getId());
        return bedTypeRequestDto;
    }

    @Override
    public Page<BedRoomTypeResponseDto> findAll(Pageable page, Map<String, String> search) {
        log.info("Loading paginated BedRoomTypes with page {} and search {} ", page, search);
        return bedRoomTypeRepository
                .findAll(createSpecification(BedRoomType.class, search), page)
                .map(BedRoomTypeResponseDto::new);
    }

    @Override
    public BedRoomTypeResponseDto findByUuid(UUID uuid) {
        log.info("finding role with uuid {} ", uuid);
        return bedRoomTypeRepository
                .findByUuid(uuid)
                .map(BedRoomTypeResponseDto::new)
                .orElseThrow(() -> new ValidationException("BedRoomType with uuid {" + uuid + "} not found"));
    }

    @Override
    public void delete(UUID uuid) {
        log.info("deleting BedRoomType with uuid {} ", uuid);
        bedRoomTypeRepository.softDelete(uuid);
    }
}
