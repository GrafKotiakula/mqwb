package knu.csc.ttp.qualificationwork.mqwb.abstractions.controllers;

import knu.csc.ttp.qualificationwork.mqwb.LoggerUtils;
import knu.csc.ttp.qualificationwork.mqwb.ReflectionUtils;
import knu.csc.ttp.qualificationwork.mqwb.abstractions.AbstractEntity;
import knu.csc.ttp.qualificationwork.mqwb.abstractions.AbstractService;
import knu.csc.ttp.qualificationwork.mqwb.abstractions.validation.AbstractEntityValidator;
import knu.csc.ttp.qualificationwork.mqwb.exceptions.server.InternalServerErrorException;
import org.apache.logging.log4j.Level;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public abstract class AbstractEntityController <ENTITY extends AbstractEntity,
        SERVICE extends AbstractService<ENTITY, ? extends JpaRepository<ENTITY, UUID>>,
        VALIDATOR extends AbstractEntityValidator<ENTITY>>
        extends AbstractController {
    private final IllegalStateException initIllegalState = new IllegalStateException("Superclass is not generic");

    protected Class<ENTITY> entityClass;
    protected SERVICE service;
    protected VALIDATOR validator;

    protected AbstractEntityController(ApplicationContext context, Class<ENTITY> entityClass,
                                       SERVICE service, VALIDATOR validator) {
        super(context);
        this.entityClass = entityClass;
        this.service = service;
        this.validator = validator;
    }

    protected AbstractEntityController(ApplicationContext context) {
        super(context);
        this.entityClass = getEntityClass(this, AbstractEntityController.class, 0);
        this.service = findService(context, this, AbstractEntityController.class, 1);
        this.validator = findValidator(context, this, AbstractEntityController.class, 2);
        logConstructedSuccessfully(Level.DEBUG);
    }

    /** Log message that controller successfully auto-constructed from generic parameters **/
    @SuppressWarnings("SameParameterValue")
    protected void logConstructedSuccessfully(Level level) {
        logger.log(level, "{} constructed from generic parameters successfully", getClass().getSimpleName());
    }

    @SuppressWarnings({"unchecked", "SameParameterValue"})
    protected <G> Class<ENTITY> getEntityClass(G object, Class<G> genericClass, int index) {
        Class<ENTITY> clazz = Optional.ofNullable(ReflectionUtils.getTypeClassOfGenericClass(object, genericClass,
                        null, index))
                .map(c -> (Class<ENTITY>)c)
                .orElseThrow(() -> LoggerUtils.fatalException(logger, InternalServerErrorException
                        .cannotGetGenericClassTypeParameter(getClass(), initIllegalState) ));
        logger.trace("{} class was obtained successfully", clazz.getSimpleName());
        return clazz;
    }

    @SuppressWarnings({"unchecked", "SameParameterValue"})
    protected <G> SERVICE findService(ApplicationContext context, G object, Class<G> genericClass, int index) {
        Class<SERVICE> serviceClass = (Class<SERVICE>) ReflectionUtils.getTypeClassOfGenericClass(object,
                genericClass, null, index);

        return findBean(context, serviceClass);
    }

    @SuppressWarnings({"unchecked", "SameParameterValue"})
    protected <G> VALIDATOR findValidator(ApplicationContext context, G object, Class<G> genericClass, int index) {
        Class<VALIDATOR> serviceClass = (Class<VALIDATOR>) ReflectionUtils.getTypeClassOfGenericClass(object,
                genericClass, null, index);

        return findBean(context, serviceClass);
    }

    private <T> T findBean(ApplicationContext context, Class<T> nullableClazz) {
        try {
            return Optional.ofNullable(nullableClazz)
                    .map(clazz -> {
                        T bean = context.getBean(clazz);
                        logger.trace("{} bean was obtained successfully", bean.getClass().getSimpleName());
                        return bean;
                    })
                    .orElseThrow(() -> initIllegalState);
        } catch (ClassCastException | IllegalStateException ex) {
            throw LoggerUtils.fatalException(logger, InternalServerErrorException
                    .cannotGetGenericClassTypeParameter(getClass(), ex));
        }
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
}
