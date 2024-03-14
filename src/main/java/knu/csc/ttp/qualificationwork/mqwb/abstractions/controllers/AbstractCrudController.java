package knu.csc.ttp.qualificationwork.mqwb.abstractions.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import knu.csc.ttp.qualificationwork.mqwb.LoggerUtils;
import knu.csc.ttp.qualificationwork.mqwb.ReflectionUtils;
import knu.csc.ttp.qualificationwork.mqwb.abstractions.AbstractEntity;
import knu.csc.ttp.qualificationwork.mqwb.abstractions.AbstractService;
import knu.csc.ttp.qualificationwork.mqwb.abstractions.validation.AbstractEntityValidator;
import knu.csc.ttp.qualificationwork.mqwb.exceptions.client.BadRequestException;
import knu.csc.ttp.qualificationwork.mqwb.exceptions.server.InternalServerErrorException;
import knu.csc.ttp.qualificationwork.mqwb.user.Role;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public abstract class AbstractCrudController<ENTITY extends AbstractEntity,
        SERVICE extends AbstractService<ENTITY, ? extends JpaRepository<ENTITY, UUID>>,
        VALIDATOR extends AbstractEntityValidator<ENTITY>>
        extends AbstractController {
    private final IllegalStateException initIllegalState = new IllegalStateException("Superclass is not generic");

    protected Class<ENTITY> entityClass;
    protected SERVICE service;
    protected VALIDATOR validator;

    protected GrantedAuthority getAllAuthority = Role.Authority.READ.getAuthority();
    protected GrantedAuthority getByIdAuthority = Role.Authority.READ.getAuthority();
    protected GrantedAuthority createAuthority = Role.Authority.CREATE.getAuthority();
    protected GrantedAuthority updateAuthority = Role.Authority.UPDATE.getAuthority();
    protected GrantedAuthority deleteAuthority = Role.Authority.DELETE.getAuthority();


    public AbstractCrudController(ApplicationContext context) {
        super(context);
        entityClass = getEntityClass();
        service = findService(context);
        validator = findValidator(context);
    }

    @SuppressWarnings("unchecked")
    private Class<ENTITY> getEntityClass() {
        return Optional.ofNullable(ReflectionUtils.getTypeClassOfGenericClass(this,
                        AbstractCrudController.class, null, 0))
                .map(c -> (Class<ENTITY>)c)
                .orElseThrow(() -> LoggerUtils.fatalException(logger, InternalServerErrorException
                        .cannotGetGenericClassTypeParameter(getClass(), initIllegalState) ));
    }

    private <T> T findBean(ApplicationContext context, Class<T> nullableClazz) {
        try {
            return Optional.ofNullable(nullableClazz)
                    .map(context::getBean)
                    .orElseThrow(() -> initIllegalState);
        } catch (ClassCastException | IllegalStateException ex) {
            throw LoggerUtils.fatalException(logger, InternalServerErrorException
                    .cannotGetGenericClassTypeParameter(getClass(), ex));
        }
    }

    @SuppressWarnings("unchecked")
    private SERVICE findService(ApplicationContext context) {
        Class<SERVICE> serviceClass = (Class<SERVICE>) ReflectionUtils.getTypeClassOfGenericClass(this,
                AbstractCrudController.class, null, 1);

        return findBean(context, serviceClass);
    }

    @SuppressWarnings("unchecked")
    private VALIDATOR findValidator(ApplicationContext context) {
        Class<VALIDATOR> serviceClass = (Class<VALIDATOR>) ReflectionUtils.getTypeClassOfGenericClass(this,
                AbstractCrudController.class, null, 2);

        return findBean(context, serviceClass);
    }

    public ENTITY parseEntityOnCreate(JsonNode json) {
        try {
            return mapper.readerFor(entityClass).readValue(json);
        } catch (IOException ex) {
            throw LoggerUtils.logException(logger, defaultLogLvl, BadRequestException.cannotProcessJson(ex));
        }
    }

    public ENTITY parseEntityOnUpdate(ENTITY original, JsonNode json) {
        try {
            return mapper.readerForUpdating(original).readValue(json);
        } catch (IOException ex) {
            throw LoggerUtils.logException(logger, defaultLogLvl, BadRequestException.cannotProcessJson(ex));
        }
    }

    public URI buildEntityUriFromCurrentController(ENTITY entity) {
        String home = Optional.ofNullable(getClass().getAnnotation(RequestMapping.class))
                .filter(rm -> rm.value().length != 0)
                .map(rm -> rm.value()[0])
                .orElse("");
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/{home}/{id}")
                .buildAndExpand(home, entity.getId())
                .toUri().normalize();
    }

    @GetMapping("/all")
    public List<ENTITY> getAll(@RequestParam(value = "page", required = false) Integer page) {
        checkAuthority(getAllAuthority);
        return Optional.ofNullable(page)
                .map(p -> service.findAll(p).toList())
                .orElse(service.findAll());
    }

    @GetMapping("/{id}")
    public ENTITY getById(@PathVariable("id") UUID id) {
        checkAuthority(getByIdAuthority);
        return service.findByIdOrThrow(id);
    }

    @PostMapping
    public ResponseEntity<ENTITY> create(@RequestBody JsonNode json) {
        checkAuthority(createAuthority);

        ENTITY entity = parseEntityOnCreate(json);

        validator.validate(entity, VALIDATOR.CREATE);
        entity = service.create(entity);

        return ResponseEntity
                .created(buildEntityUriFromCurrentController(entity))
                .body(entity);
    }

    @PutMapping("/{id}")
    public ENTITY updateById(@PathVariable("id") UUID id, @RequestBody JsonNode json) {
        checkAuthority(updateAuthority);

        ENTITY entity = service.findByIdOrThrow(id);

        entity = parseEntityOnUpdate(entity, json);
        validator.validate(entity, VALIDATOR.UPDATE);
        entity = service.update(entity);

        return entity;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable("id") UUID id) {
        checkAuthority(deleteAuthority);

        service.findById(id).ifPresent(service::delete);
        return ResponseEntity.noContent().build();
    }

    public SERVICE getService() {
        return service;
    }

    public void setService(SERVICE service) {
        this.service = service;
    }

    public VALIDATOR getValidator() {
        return validator;
    }

    public void setValidator(VALIDATOR validator) {
        this.validator = validator;
    }

    public GrantedAuthority getGetAllAuthority() {
        return getAllAuthority;
    }

    public void setGetAllAuthority(GrantedAuthority getAllAuthority) {
        this.getAllAuthority = getAllAuthority;
    }

    public GrantedAuthority getGetByIdAuthority() {
        return getByIdAuthority;
    }

    public void setGetByIdAuthority(GrantedAuthority getByIdAuthority) {
        this.getByIdAuthority = getByIdAuthority;
    }

    public GrantedAuthority getCreateAuthority() {
        return createAuthority;
    }

    public void setCreateAuthority(GrantedAuthority createAuthority) {
        this.createAuthority = createAuthority;
    }

    public GrantedAuthority getUpdateAuthority() {
        return updateAuthority;
    }

    public void setUpdateAuthority(GrantedAuthority updateAuthority) {
        this.updateAuthority = updateAuthority;
    }

    public GrantedAuthority getDeleteAuthority() {
        return deleteAuthority;
    }

    public void setDeleteAuthority(GrantedAuthority deleteAuthority) {
        this.deleteAuthority = deleteAuthority;
    }
}
