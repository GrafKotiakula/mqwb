package knu.csc.ttp.qualificationwork.mqwb.abstractions.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import knu.csc.ttp.qualificationwork.mqwb.LoggerUtils;
import knu.csc.ttp.qualificationwork.mqwb.abstractions.AbstractEntity;
import knu.csc.ttp.qualificationwork.mqwb.abstractions.AbstractService;
import knu.csc.ttp.qualificationwork.mqwb.abstractions.validation.AbstractEntityValidator;
import knu.csc.ttp.qualificationwork.mqwb.entities.user.Role;
import knu.csc.ttp.qualificationwork.mqwb.exceptions.client.BadRequestException;
import org.apache.logging.log4j.Level;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;
import java.util.UUID;

public abstract class AbstractCrudController<ENTITY extends AbstractEntity,
        SERVICE extends AbstractService<ENTITY, ? extends JpaRepository<ENTITY, UUID>>,
        VALIDATOR extends AbstractEntityValidator<ENTITY>>
        extends AbstractEntityController<ENTITY, SERVICE, VALIDATOR> {

    protected GrantedAuthority getAllAuthority = Role.Authority.ANONYMOUS_READ.getAuthority();
    protected GrantedAuthority getByIdAuthority = Role.Authority.ANONYMOUS_READ.getAuthority();
    protected GrantedAuthority createAuthority = Role.Authority.CREATE.getAuthority();
    protected GrantedAuthority updateAuthority = Role.Authority.UPDATE.getAuthority();
    protected GrantedAuthority deleteAuthority = Role.Authority.DELETE.getAuthority();

    public AbstractCrudController(ApplicationContext context) {
        super(context, null, null, null);
        this.entityClass = getEntityClass(this, AbstractCrudController.class, 0);
        this.service = findService(context, this, AbstractCrudController.class, 1);
        this.validator = findValidator(context, this, AbstractCrudController.class, 2);
        logConstructedSuccessfully(Level.DEBUG);
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
    public Page<ENTITY> getAll(@RequestParam(value = "page", defaultValue = "0") Integer page) {
        checkAuthority(getAllAuthority);
        return service.findAll(page);
    }

    @GetMapping("/{id}")
    public ENTITY getById(@PathVariable("id") String strId) {
        checkAuthority(getByIdAuthority);
        UUID id = convertToUUID(strId, "id");

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
    public ENTITY updateById(@PathVariable("id") String strId, @RequestBody JsonNode json) {
        checkAuthority(updateAuthority);
        UUID id = convertToUUID(strId, "id");

        ENTITY entity = service.findByIdOrThrow(id);

        entity = parseEntityOnUpdate(entity, json);
        validator.validate(entity, VALIDATOR.UPDATE);
        entity = service.update(entity);

        return entity;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable("id") String strId) {
        checkAuthority(deleteAuthority);
        UUID id = convertToUUID(strId, "id");

        service.findById(id).ifPresent(service::delete);
        return ResponseEntity.noContent().build();
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
