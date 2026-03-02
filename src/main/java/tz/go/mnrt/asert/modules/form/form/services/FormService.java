package tz.go.mnrt.asert.modules.form.form.services;

import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import tz.go.mnrt.asert.modules.form.form.dtos.FormRequestDto;
import tz.go.mnrt.asert.modules.form.form.dtos.FormResponseDto;
import tz.go.mnrt.asert.modules.form.form.dtos.FormWithScoringDto;


public interface FormService {

    FormRequestDto save(FormRequestDto formDto);

    Page<FormResponseDto> findAll(Pageable page, Map<String, String> search);

    FormResponseDto findByUuid(UUID id);

    void delete(UUID uuid);

    FormWithScoringDto getFormWithScoring(UUID form);

}
