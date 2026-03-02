package tz.go.mnrt.asert.modules.role.services;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

@Service
public class AdminPermissionService {

  @PersistenceContext
  private EntityManager entityManager;

  @Transactional
  public void setAdminPermissions() {
    // Check if the role_authorities table has a role with code SUPER_ADMINISTRATOR
    String checkQuery = "SELECT COUNT(*) FROM role_authorities ra " +
        "JOIN roles r ON ra.role_id = r.id " +
        "WHERE r.code = 'SUPER_ADMINISTRATOR'";

    Long count = ((Number) entityManager.createNativeQuery(checkQuery).getSingleResult()).longValue();

    if (count == 0) {
      // Execute the query to insert permissions
      String insertQuery = """
          INSERT INTO role_authorities (role_id, authority_id)
          SELECT DISTINCT 1 AS role_id, a.id AS authority_id
          FROM authorities a
          WHERE
              (a.resource IN ('Role', 'MenuItem', 'MenuGroup')
               AND a.action IN ('delete', 'get', 'update', 'create', 'findById'))
              OR
              (a.resource = 'Authority' AND a.action in ('getPermissions', 'get'))
              OR
              (a.resource = 'Role' AND a.action = 'assignAuthorities')
              OR
              (a.resource = 'MenuItem' AND a.action = 'assignAuthority')
              OR
              (a.resource = 'Notification' AND a.action = 'getNotificationsByRecipientId')
          AND NOT EXISTS (
              SELECT 1
              FROM role_authorities ra
              WHERE ra.role_id = 1
                AND ra.authority_id = a.id
          )
          """;
      entityManager.createNativeQuery(insertQuery).executeUpdate();
    }
  }
}
