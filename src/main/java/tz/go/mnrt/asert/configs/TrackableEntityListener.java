package tz.go.mnrt.asert.configs;

import java.lang.reflect.Field;

import javax.persistence.EntityManager;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PreRemove;
import javax.transaction.Transactional;

import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import tz.go.mnrt.asert.annotations.IsTrackable;
import tz.go.mnrt.asert.enums.EntityAction;
import tz.go.mnrt.asert.helpers.BeanUtil;
import tz.go.mnrt.asert.modules.core.entities.BaseModel;
import tz.go.mnrt.asert.modules.core.entities.EntityTracker;

public class TrackableEntityListener {

    @PostPersist
    public void postCreate(Object entity) {
        track(entity, EntityAction.CREATE);
    }

    @PostUpdate
    public void Update(Object entity) {
        track(entity, EntityAction.UPDATE);
    }

    @PostRemove
    public void postRemove(Object entity) {
        track(entity, EntityAction.DELETE);
    }

    @PreRemove
    public void preRemove(Object entity) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            try {
                Field removedByField = BaseModel.class.getDeclaredField("removedBy");
                removedByField.setAccessible(true);
                removedByField.set(entity, auth.getName());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Async("taskExecutor")
    @Transactional
    public void track(Object entity, EntityAction action) {
        if (entity.getClass().isAnnotationPresent(IsTrackable.class)) {

            IsTrackable isTrackableAnnotation = entity.getClass().getAnnotation(IsTrackable.class);
            String[] includes = isTrackableAnnotation.include();

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            SimpleModule module = new SimpleModule();
            module.addSerializer(BaseModel.class, RelationshipIgnoringSerializer.withInclude(includes));
            objectMapper.registerModule(module);
            objectMapper.registerModule(new JavaTimeModule());
            try {
                String json = objectMapper.writeValueAsString(entity);
                Field idField = BaseModel.class.getDeclaredField("id");
                idField.setAccessible(true);
                Field createdBy = action == EntityAction.CREATE ? BaseModel.class.getDeclaredField("createdBy")
                        : action == EntityAction.UPDATE ? BaseModel.class.getDeclaredField("updatedBy")
                                : BaseModel.class.getDeclaredField("removedBy");
                createdBy.setAccessible(true);

                Long id = (Long) (idField.get(entity));

                EntityTracker tracker = new EntityTracker(action, entity.getClass().getTypeName(), json, id,
                        createdBy.get(entity).toString());

                EntityManager em = BeanUtil.getBean(EntityManager.class);
                Assert.state(em != null, "Entity manager shouldn't be null");
                em.persist(tracker);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
