package tz.go.mnrt.asert.modules.menuitem.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tz.go.mnrt.asert.modules.menugroup.dtos.MenuGroupDto;
import tz.go.mnrt.asert.modules.menuitem.dtos.MenuItemAuthorityDto;
import tz.go.mnrt.asert.modules.menuitem.dtos.MenuItemDto;
import tz.go.mnrt.asert.modules.menuitem.entity.MenuItem;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface MenuItemService {

  MenuItemDto save(MenuItemDto menuItemDto);

  Page<MenuItemDto> findAll(Pageable page, Map<String, String> search);

  MenuItemDto findByUuid(UUID id);

  void delete(UUID uuid);

  List<MenuGroupDto> getWithItems(Map<String, List<Long>> groupItemIds);

  Set<MenuItem> getByAuthorities(List<Long> authorities);

  Set<MenuItem> getWithNoGroupByAuthorities(List<Long> authorities);

  MenuItemDto saveAuthorities(MenuItemAuthorityDto dto);
}
