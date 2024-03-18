package knu.csc.ttp.qualificationwork.mqwb.abstractions.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import knu.csc.ttp.qualificationwork.mqwb.Constants;
import knu.csc.ttp.qualificationwork.mqwb.LoggerUtils;
import knu.csc.ttp.qualificationwork.mqwb.exceptions.client.BadRequestException;
import knu.csc.ttp.qualificationwork.mqwb.exceptions.client.ForbiddenException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.UUID;

public class AbstractController {
    protected final Logger logger = LoggerUtils.getNamedLogger(Constants.controllerLoggerName, getClass());

    protected ObjectMapper mapper;
    protected Level defaultLogLvl = Constants.defaultControllerLogLvl;

    public AbstractController(ApplicationContext context) {
        mapper = context.getBean(ObjectMapper.class);
    }

    @SuppressWarnings("SameParameterValue")
    protected UUID convertToUUID(String str, String parameter) {
        if(str == null) {
            return null;
        }
        try {
            return UUID.fromString(str);
        } catch (IllegalArgumentException ex) {
            throw LoggerUtils.logException(logger, defaultLogLvl, BadRequestException
                    .fieldWrongFormat(parameter, "string[uuid]", ex));
        }
    }

    protected ZonedDateTime convertToZonedDateTime(String str, String parameter) {
        if(str == null) {
            return null;
        } else {
            try{
                return ZonedDateTime.parse(str, Constants.dateTimeFormatter);
            } catch (DateTimeParseException ex) {
                throw LoggerUtils.logException(logger, defaultLogLvl, BadRequestException
                        .fieldWrongFormat(parameter, Constants.dateTimeFormat, ex));
            }
        }
    }

    protected void checkAuthority(GrantedAuthority authority) {
        if(authority != null) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Optional.ofNullable(auth)
                    .map(Authentication::getAuthorities)
                    .filter(auths -> auths.contains(authority))
                    .orElseThrow( () -> LoggerUtils
                            .warnException(logger, ForbiddenException.noRequiredAuthority(auth, authority)) );
        }
    }

    public Level getDefaultLogLvl() {
        return defaultLogLvl;
    }

    public void setDefaultLogLvl(Level defaultLogLvl) {
        this.defaultLogLvl = defaultLogLvl;
    }

    public ObjectMapper getMapper() {
        return mapper;
    }

    public void setMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }
}
