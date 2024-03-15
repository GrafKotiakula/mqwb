package knu.csc.ttp.qualificationwork.mqwb.abstractions;

import knu.csc.ttp.qualificationwork.mqwb.Constants;
import knu.csc.ttp.qualificationwork.mqwb.LoggerUtils;
import knu.csc.ttp.qualificationwork.mqwb.ReflectionUtils;
import knu.csc.ttp.qualificationwork.mqwb.exceptions.client.NotFoundException;
import knu.csc.ttp.qualificationwork.mqwb.entities.user.User;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public abstract class AbstractService <ENTITY extends AbstractEntity, REPO extends JpaRepository<ENTITY, UUID>> {
    protected final Logger logger = LogManager.getLogger(getClass());
    protected final REPO repository;
    protected final Class<? extends AbstractEntity> entityClass;

    protected Level defaultCreateLogLvl = Constants.defaultCreateLogLvl;
    protected Level defaultUpdateLogLvl = Constants.defaultUpdateLogLvl;
    protected Level defaultDeleteLogLvl = Constants.defaultDeleteLogLvl;
    protected Sort defaultSort = Sort.unsorted();

    protected AbstractService(REPO repository) {
        this.repository = repository;

        this.entityClass = ReflectionUtils
                .getFirstTypeClassOfGenericClass(this, AbstractService.class, AbstractEntity.class);
    }

    protected Pageable pageableOf(int page) {
        return pageableOf(page, defaultSort);
    }

    protected Pageable pageableOf(int page, Sort sort) {
        if(page < 0){
            page = 0;
        }
        return PageRequest.of(page, Constants.pageSize, sort);
    }

    public Optional<ENTITY> findById(UUID id){
        return repository.findById(id);
    }

    public ENTITY findByIdOrThrow(UUID id) {
        return findById(id)
                .orElseThrow( () -> LoggerUtils.debugException(logger, NotFoundException.idNotFound(entityClass, id)) );
    }

    public Optional<ENTITY> findByNullableId(UUID id){
        if(id == null) {
            return Optional.empty();
        }
        return findById(id);
    }

    public ENTITY findByNullableIdOrThrow(UUID id){
        if(id == null) {
            return null;
        }
        return findByIdOrThrow(id);
    }

    public List<ENTITY> findAll() {
        return repository.findAll(defaultSort);
    }

    public Page<ENTITY> findAll(int page) {
        return repository.findAll(pageableOf(page));
    }

    public ENTITY create(ENTITY entity, Level logLevel) {
        entity = repository.saveAndFlush(entity);
        logger.log(Optional.ofNullable(logLevel).orElse(defaultCreateLogLvl),
                "{} is created by {}", entity, getAuthenticatedUser().orElseGet(User::new));
        return entity;
    }

    public ENTITY create(ENTITY entity) {
        return create(entity, defaultCreateLogLvl);
    }

    public ENTITY update(ENTITY entity, Level logLevel) {
        entity = repository.saveAndFlush(entity);
        logger.log(Optional.ofNullable(logLevel).orElse(defaultUpdateLogLvl),
                "{} is updated by {}", entity, getAuthenticatedUser().orElseGet(User::new));
        return entity;
    }

    public ENTITY update(ENTITY entity) {
        return update(entity, defaultUpdateLogLvl);
    }

    protected Optional<User> getAuthenticatedUser() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getPrincipal)
                .filter(principal -> principal instanceof User)
                .map(principal -> (User) principal);
    }

    public ENTITY save(ENTITY entity){
        return save(entity, null);
    }

    public ENTITY save(ENTITY entity, Level logLevel) {
        if(entity.getId() == null) {
            return create(entity, logLevel);
        } else {
            return update(entity, logLevel);
        }
    }

    public void delete(ENTITY entity, Level logLevel){
        repository.delete(entity);
        repository.flush();
        logger.log(Optional.ofNullable(logLevel).orElse(defaultDeleteLogLvl),
                "{} is deleted by {}", entity, getAuthenticatedUser().orElseGet(User::new));
    }

    public void delete(ENTITY entity){
        delete(entity, defaultDeleteLogLvl);
    }

    public void deleteNullable(ENTITY entity, Level logLevel){
        if(entity != null) {
            delete(entity, logLevel);
        } else {
            logger.trace("null-deleting is ignored");
        }
    }

    public void deleteNullable(ENTITY entity) {
        deleteNullable(entity, defaultDeleteLogLvl);
    }

    public Sort getDefaultSort() {
        return defaultSort;
    }

    public void setDefaultSort(Sort defaultSort) {
        this.defaultSort = defaultSort;
    }

    public Level getDefaultCreateLogLvl() {
        return defaultCreateLogLvl;
    }

    public void setDefaultCreateLogLvl(Level defaultCreateLogLvl) {
        this.defaultCreateLogLvl = defaultCreateLogLvl;
    }

    public Level getDefaultUpdateLogLvl() {
        return defaultUpdateLogLvl;
    }

    public void setDefaultUpdateLogLvl(Level defaultUpdateLogLvl) {
        this.defaultUpdateLogLvl = defaultUpdateLogLvl;
    }

    public Level getDefaultDeleteLogLvl() {
        return defaultDeleteLogLvl;
    }

    public void setDefaultDeleteLogLvl(Level defaultDeleteLogLvl) {
        this.defaultDeleteLogLvl = defaultDeleteLogLvl;
    }
}
