package tz.go.mnrt.asert.modules.core.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tz.go.mnrt.asert.modules.setup.translation.dtos.TranslationDto;
import tz.go.mnrt.asert.modules.core.services.SimpleSearchService;
import tz.go.mnrt.asert.modules.core.services.TranslationService;
import tz.go.mnrt.asert.modules.setup.translation.entity.Translation;
import tz.go.mnrt.asert.modules.setup.translation.repository.TranslationRepository;

import javax.validation.ValidationException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TranslationServiceImpl extends SimpleSearchService<Translation>
        implements TranslationService {
    private final TranslationRepository translationRepository;

    public static String toTitleCase(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        String[] words = str.split("\\s+");
        StringBuilder sb = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                sb.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase())
                        .append(" ");
            }
        }

        return sb.toString().trim();
    }

    @Override
    public TranslationDto save(TranslationDto translationDto) {
        Translation translation = new Translation();
        BeanUtils.copyProperties(translationDto, translation, "uuid");
        translation = translationRepository.save(translation);
        translationDto.setId(translation.getId());
        return translationDto;
    }

    @Override
    public Page<TranslationDto> findAll(Pageable page, Map<String, String> search) {
        return translationRepository
                .findAll(createSpecification(Translation.class, search), page)
                .map(TranslationDto::new);
    }

    @Override
    public List<TranslationDto> findAll() {
        return translationRepository.findAll().stream()
                .map(TranslationDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public TranslationDto findByUuid(UUID uuid) {
        log.info("finding translation point with uuid {} ", uuid);
        return translationRepository
                .findByUuid(uuid)
                .map(TranslationDto::new)
                .orElseThrow(
                        () -> new ValidationException("Translation with uuid {" + uuid + "} not found"));
    }

    @Override
    public TranslationDto titleizeTranslation(TranslationDto translationDto) {
        String english = toTitleCase(translationDto.getEnglish());
        String swahili = toTitleCase(translationDto.getSwahili());
        translationDto.setEnglish(english);
        translationDto.setSwahili(swahili);
        return translationDto;
    }

    @Override
    public void delete(UUID uuid) {
        log.info("deleting translation with uuid {} ", uuid);
        translationRepository.deleteByUuid(uuid);
    }
}
