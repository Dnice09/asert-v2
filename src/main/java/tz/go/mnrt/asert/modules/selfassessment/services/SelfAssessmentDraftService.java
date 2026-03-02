package tz.go.mnrt.asert.modules.selfassessment.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tz.go.mnrt.asert.modules.selfassessment.dtos.SelfAssessmentDraftRequestDto;
import tz.go.mnrt.asert.modules.selfassessment.dtos.SelfAssessmentDraftResponseDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface SelfAssessmentDraftService {

    SelfAssessmentDraftResponseDto save(SelfAssessmentDraftRequestDto requestDto);

    SelfAssessmentDraftResponseDto findByUuid(UUID uuid);

    Page<SelfAssessmentDraftResponseDto> findAll(Pageable pageable, Map<String, String> search);

    void delete(UUID uuid);

    SelfAssessmentDraftResponseDto findByFormAndUser(UUID formUuid, String submittedBy);

    SelfAssessmentDraftResponseDto findByFormHotelAndUser(UUID formUuid, UUID hotelUuid, String submittedBy);

    List<SelfAssessmentDraftResponseDto> findByUser(String submittedBy);

    List<SelfAssessmentDraftResponseDto> findByHotel(UUID hotelUuid);

    void deleteExpiredDrafts(int daysOld);

    // Current user specific methods
    SelfAssessmentDraftResponseDto findByFormAndCurrentUser(UUID formUuid);

    SelfAssessmentDraftResponseDto findByFormHotelAndCurrentUser(UUID formUuid, UUID hotelUuid);

    List<SelfAssessmentDraftResponseDto> findByCurrentUser();
}
