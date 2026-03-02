package tz.go.mnrt.asert.modules.menugroup.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tz.go.mnrt.asert.modules.menugroup.entity.MenuGroup;
import tz.go.mnrt.asert.modules.menugroup.repository.MenuGroupRepository;
import tz.go.mnrt.asert.modules.menugroup.dtos.MenuGroupDto;

import javax.validation.ValidationException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MenuGroupServiceImpl implements MenuGroupService {
  private final MenuGroupRepository menuGroupRepository;

  @Override
  public MenuGroupDto save(MenuGroupDto menuGroupDto) {
    MenuGroup menuGroup = new MenuGroup();
    if (menuGroupDto.getUuid() != null) {
      menuGroup =
          menuGroupRepository
              .findByUuid(menuGroupDto.getUuid())
              .orElseThrow(
                  () ->
                      new ValidationException(
                          "MenuGroup with uuid {" + menuGroupDto.getUuid() + "} not found"));
    }
    BeanUtils.copyProperties(menuGroupDto, menuGroup, "uuid");
    assert (menuGroup.getUuid() != null);
    menuGroup = menuGroupRepository.save(menuGroup);
    menuGroupDto.setId(menuGroup.getId());
    return menuGroupDto;
  }

  @Override
  public Page<MenuGroupDto> findAll(Pageable page, Map<String, String> search) {
    log.info("Loading paginated menuGroups with page {} and search {} ", page, search);
    return menuGroupRepository.findAll(createSpecification(search), page).map(MenuGroupDto::new);
  }

  @Override
  public MenuGroupDto findByUuid(UUID uuid) {
    log.info("finding menuGroup with uuid {} ", uuid);
    return menuGroupRepository
        .findByUuid(uuid)
        .map(MenuGroupDto::new)
        .orElseThrow(() -> new ValidationException("MenuGroup with uuid {" + uuid + "} not found"));
  }

  @Override
  public void delete(UUID uuid) {
    log.info("deleting menuGroup with uuid {} ", uuid);
    menuGroupRepository.deleteByUuid(uuid);
  }

  protected Specification<MenuGroup> createSpecification(Map<String, String> search) {
    Specification<MenuGroup> menuGroupSpecification = Specification.where(null);
    List<String> allowedProps =
        Arrays.stream(MenuGroup.class.getDeclaredFields())
            .map(Field::getName)
            .collect(Collectors.toList());
    if (!search.isEmpty()) {
      for (String key : search.keySet()) {
        if (allowedProps.contains(key) && !search.get(key).isEmpty()) {
          menuGroupSpecification =
              menuGroupSpecification.and(
                  (root, query, builder) ->
                      builder.like(
                          builder.lower(root.get(key)), "%" + search.get(key).toLowerCase() + "%"));
        }
      }
    }
    return menuGroupSpecification;
  }
}
