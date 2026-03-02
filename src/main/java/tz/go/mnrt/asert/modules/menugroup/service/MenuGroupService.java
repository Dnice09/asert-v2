package tz.go.mnrt.asert.modules.menugroup.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tz.go.mnrt.asert.modules.menugroup.dtos.MenuGroupDto;

import java.util.Map;
import java.util.UUID;

public interface MenuGroupService {

  MenuGroupDto save(MenuGroupDto menuGroupDto);

  Page<MenuGroupDto> findAll(Pageable page, Map<String, String> search);

  MenuGroupDto findByUuid(UUID id);

  void delete(UUID uuid);
}
