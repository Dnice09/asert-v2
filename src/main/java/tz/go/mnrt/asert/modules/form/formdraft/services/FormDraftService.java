package tz.go.mnrt.asert.modules.form.formdraft.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tz.go.mnrt.asert.modules.form.formdraft.dtos.FormDraftRequestDto;
import tz.go.mnrt.asert.modules.form.formdraft.dtos.FormDraftResponseDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface FormDraftService {

    FormDraftResponseDto save(FormDraftRequestDto requestDto);

    FormDraftResponseDto findByUuid(UUID uuid);

    Page<FormDraftResponseDto> findAll(Pageable pageable, Map<String, String> search);

    void delete(UUID uuid);

    FormDraftResponseDto findByFormAndUser(UUID formUuid, String submittedBy);

    FormDraftResponseDto findByFormHotelAndUser(UUID formUuid, UUID hotelUuid, String submittedBy);

    List<FormDraftResponseDto> findByUser(String submittedBy);

    List<FormDraftResponseDto> findByHotel(UUID hotelUuid);

    void deleteExpiredDrafts(int daysOld);

    // Current user specific methods
    FormDraftResponseDto findByFormAndCurrentUser(UUID formUuid);

    FormDraftResponseDto findByFormHotelAndCurrentUser(UUID formUuid, UUID hotelUuid);

    List<FormDraftResponseDto> findByCurrentUser();
}
