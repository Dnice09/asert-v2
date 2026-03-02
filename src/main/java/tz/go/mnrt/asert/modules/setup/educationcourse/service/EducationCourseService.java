package tz.go.mnrt.asert.modules.setup.educationcourse.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tz.go.mnrt.asert.modules.setup.educationcourse.dto.EducationCourseDto;

import java.util.Map;
import java.util.UUID;

@Service
public interface EducationCourseService {

  EducationCourseDto save(EducationCourseDto educationCourseDto);

  Page<EducationCourseDto> findAll(Pageable page, Map<String, String> search);

  EducationCourseDto findByUuid(UUID id);

  void delete(UUID uuid);
}
