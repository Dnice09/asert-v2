package tz.go.mnrt.asert.modules.hotel.bedtype.services;

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
import tz.go.mnrt.asert.modules.hotel.bedtype.dtos.BedTypeRequestDto;
import tz.go.mnrt.asert.modules.hotel.bedtype.dtos.BedTypeResponseDto;
import tz.go.mnrt.asert.modules.hotel.bedtype.entity.BedType;
import tz.go.mnrt.asert.modules.hotel.bedtype.repository.BedTypeRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class BedTypeServiceImpl extends SimpleSearchService<BedType> implements BedTypeService {
    private final BedTypeRepository bedTypeRepository;

    @Override
    public BedTypeRequestDto save(BedTypeRequestDto bedTypeRequestDto) {
        BedType bedType = new BedType();
        if (bedTypeRequestDto.getUuid() != null) {
            bedType = bedTypeRepository
                    .findByUuid(bedTypeRequestDto.getUuid())
                    .orElseThrow(
                            () -> new ValidationException(
                                    "BedType with uuid {" + bedTypeRequestDto.getUuid() + "} not found"));
        }
        BeanUtils.copyProperties(bedTypeRequestDto, bedType, "uuid");
        assert (bedType.getUuid() != null);
        bedType = bedTypeRepository.save(bedType);
        bedTypeRequestDto.setId(bedType.getId());
        return bedTypeRequestDto;
    }

    @Override
    public Page<BedTypeResponseDto> findAll(Pageable page, Map<String, String> search) {
        log.info("Loading paginated BedTypes with page {} and search {} ", page, search);
        return bedTypeRepository
                .findAll(createSpecification(BedType.class, search), page)
                .map(BedTypeResponseDto::new);
    }

    @Override
    public BedTypeResponseDto findByUuid(UUID uuid) {
        log.info("finding role with uuid {} ", uuid);
        return bedTypeRepository
                .findByUuid(uuid)
                .map(BedTypeResponseDto::new)
                .orElseThrow(() -> new ValidationException("BedType with uuid {" + uuid + "} not found"));
    }

    @Override
    public void delete(UUID uuid) {
        log.info("deleting BedType with uuid {} ", uuid);
        bedTypeRepository.softDelete(uuid);
    }
}
