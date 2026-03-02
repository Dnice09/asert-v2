package tz.go.mnrt.asert.modules.core.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tz.go.mnrt.asert.modules.setup.translation.dtos.TranslationDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface TranslationService {
  TranslationDto save(TranslationDto translationDto);

  Page<TranslationDto> findAll(Pageable page, Map<String, String> search);

  List<TranslationDto> findAll();

  TranslationDto findByUuid(UUID uuid);

  void delete(UUID uuid);

  TranslationDto titleizeTranslation(TranslationDto translationDto);
}
