package tz.go.mnrt.asert.modules.bednight.visitor.services;

import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import tz.go.mnrt.asert.modules.bednight.visitor.dtos.VisitorRequestDto;
import tz.go.mnrt.asert.modules.bednight.visitor.dtos.VisitorResponseDto;

public interface VisitorService {

  VisitorRequestDto save(VisitorRequestDto visitorDto);

  Page<VisitorResponseDto> findAll(Pageable page, Map<String, String> search);

  VisitorResponseDto findByUuid(UUID id);

  void delete(UUID uuid);
}

