package tz.go.mnrt.asert.modules.setup.institute.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tz.go.mnrt.asert.modules.setup.institute.dto.InstituteDto;

import java.util.Map;
import java.util.UUID;

@Service
public interface InstituteService {

  InstituteDto save(InstituteDto instituteDto);

  Page<InstituteDto> findAll(Pageable page, Map<String, String> search);

  InstituteDto findByUuid(UUID id);

  void delete(UUID uuid);
}
