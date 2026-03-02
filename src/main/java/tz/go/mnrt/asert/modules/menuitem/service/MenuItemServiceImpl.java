package tz.go.mnrt.asert.modules.menuitem.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tz.go.mnrt.asert.modules.authority.entity.Authority;
import tz.go.mnrt.asert.modules.core.services.SimpleSearchService;
import tz.go.mnrt.asert.modules.menugroup.dtos.MenuGroupDto;
import tz.go.mnrt.asert.modules.menugroup.entity.MenuGroup;
import tz.go.mnrt.asert.modules.menugroup.repository.MenuGroupRepository;
import tz.go.mnrt.asert.modules.menuitem.dtos.MenuItemAuthorityDto;
import tz.go.mnrt.asert.modules.menuitem.dtos.MenuItemDto;
import tz.go.mnrt.asert.modules.menuitem.entity.MenuItem;
import tz.go.mnrt.asert.modules.menuitem.repository.MenuItemRepository;

import javax.persistence.EntityManager;
import javax.validation.ValidationException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class MenuItemServiceImpl extends SimpleSearchService<MenuItem> implements MenuItemService {
    private final MenuItemRepository menuItemRepository;

    private final EntityManager em;

    private final MenuGroupRepository menuGroupRepository;

    @Override
    public MenuItemDto save(MenuItemDto menuItemDto) {
        MenuItem menuItem = new MenuItem();
        if (menuItemDto.getUuid() != null) {
            menuItem = menuItemRepository
                    .findByUuid(menuItemDto.getUuid())
                    .orElseThrow(
                            () -> new ValidationException(
                                    "MenuItem with uuid {" + menuItemDto.getUuid() + "} not found"));
        }
        BeanUtils.copyProperties(menuItemDto, menuItem, "uuid");
        if (menuItemDto.getMenuGroupId() != null) {
            menuItem.setMenuGroup(em.getReference(MenuGroup.class, menuItemDto.getMenuGroupId()));
        }
        assert (menuItem.getUuid() != null);
        menuItem = menuItemRepository.save(menuItem);
        for (Long authorityId : menuItemDto.getAuthorityIds()) {
            menuItem.addAuthority(em.getReference(Authority.class, authorityId));
        }
        menuItemDto.setId(menuItem.getId());
        return menuItemDto;
    }

    @Override
    public Page<MenuItemDto> findAll(Pageable page, Map<String, String> search) {
        log.info("Loading paginated menuItems with page {} and search {} ", page, search);
        return menuItemRepository
                .findAll(createSpecification(MenuItem.class, search), page)
                .map(MenuItemDto::new);
    }

    @Override
    public MenuItemDto findByUuid(UUID uuid) {
        log.info("finding menuIterm with uuid {} ", uuid);
        return menuItemRepository
                .findByUuid(uuid)
                .map(m -> new MenuItemDto(m).withPermissions(m))
                .orElseThrow(() -> new ValidationException("MenuItem with uuid {" + uuid + "} not found"));
    }

    @Override
    public void delete(UUID uuid) {
        log.info("deleting menuIterm with uuid {} ", uuid);
        menuItemRepository.deleteByUuid(uuid);
    }

    @Override
    public List<MenuGroupDto> getWithItems(Map<String, List<Long>> ids) {
        List<MenuGroupDto> groups = menuGroupRepository.byIds(ids.get("groupIds"));
        groups.forEach(
                g -> {
                    g.setChildren(menuItemRepository.byGroupAndIds(g.getId(), ids.get("itemIds")));
                });
        return groups;
    }

    @Override
    public Set<MenuItem> getByAuthorities(List<Long> authorities) {
        return menuItemRepository.findByAuthorities(authorities);
    }

    @Override
    public Set<MenuItem> getWithNoGroupByAuthorities(List<Long> authorities) {
        return menuItemRepository.findWithNoGroupByAuthorities(authorities);
    }

    @Override
    public MenuItemDto saveAuthorities(MenuItemAuthorityDto dto) {
        MenuItem menuItem = menuItemRepository
                .findByUuid(dto.getMenuItemUuid())
                .orElseThrow(() -> new ValidationException("menu item not found"));

        menuItem.setAuthorities(new HashSet<>());

        for (Long authorityId : dto.getPermissionIds()) {
            menuItem.addAuthority(em.getReference(Authority.class, authorityId));
        }

        // Add explicit save and flush
        menuItem = menuItemRepository.saveAndFlush(menuItem);

        // Force a refresh from the database to ensure we have the latest data
        em.refresh(menuItem);

        return new MenuItemDto(menuItem).withPermissions(menuItem);
    }
}
