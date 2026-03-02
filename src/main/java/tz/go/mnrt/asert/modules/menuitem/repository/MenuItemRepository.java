package tz.go.mnrt.asert.modules.menuitem.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tz.go.mnrt.asert.modules.core.repo.BaseRepository;
import tz.go.mnrt.asert.modules.menuitem.dtos.MenuItemDto;
import tz.go.mnrt.asert.modules.menuitem.entity.MenuItem;

public interface MenuItemRepository extends BaseRepository<MenuItem, Long> {

    @EntityGraph(attributePaths = { "menuGroup" }, type = EntityGraph.EntityGraphType.FETCH)
    Page<MenuItem> findAll(Specification<MenuItem> specification, Pageable pageable);

    @EntityGraph(attributePaths = { "menuGroup", "authorities" }, type = EntityGraph.EntityGraphType.FETCH)
    Optional<MenuItem> findByUuid(UUID uuid);

    void deleteByUuid(UUID uuid);

    @Query(value = "select distinct mi.* from menu_items mi join menu_item_authorities ma  on"
            + " ma.menu_item_id = mi.id where ma.authority_id in (:authorities) and"
            + " mi.menu_group_id is not null ", nativeQuery = true)
    Set<MenuItem> findByAuthorities(@Param("authorities") List<Long> authorities);

    @Query(value = "select distinct mi.* from menu_items mi join menu_item_authorities ma  on"
            + " ma.menu_item_id = mi.id where ma.authority_id in (:authorities) and"
            + " mi.menu_group_id is null", nativeQuery = true)
    Set<MenuItem> findWithNoGroupByAuthorities(@Param("authorities") List<Long> authorities);

    @Query("Select new tz.go.mnrt.asert.modules.menuitem.dtos.MenuItemDto(i.id,i.name, i.icon, i.state,"
            + " i.sortOrder, i.translationLabel) from MenuItem  i where i.menuGroup.id=:id and i.id"
            + " in :itemIds order by i.sortOrder")
    List<MenuItemDto> byGroupAndIds(@Param("id") Long id, @Param("itemIds") List<Long> itemIds);
}
