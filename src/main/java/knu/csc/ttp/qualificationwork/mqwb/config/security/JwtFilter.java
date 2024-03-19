package knu.csc.ttp.qualificationwork.mqwb.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import knu.csc.ttp.qualificationwork.mqwb.AuthUtils;
import knu.csc.ttp.qualificationwork.mqwb.Constants;
import knu.csc.ttp.qualificationwork.mqwb.LoggerUtils;
import knu.csc.ttp.qualificationwork.mqwb.controllers.exceptionresolver.FailureDto;
import knu.csc.ttp.qualificationwork.mqwb.exceptions.client.AuthException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JwtFilter extends OncePerRequestFilter {
    private final Logger logger = LoggerUtils.getNamedLogger(Constants.securityLoggerName, getClass());
    protected Level defaultLogLevel = Constants.defaultControllerLogLvl;
    private ObjectMapper objMapper;
    private JwtProvider provider;
    private List<AntPathRequestMatcher> ignoreMatchers;

    @Autowired
    public JwtFilter(JwtProvider provider, ObjectMapper objMapper, String... ignoreAntMatchers) {
        this.provider = provider;
        this.objMapper = objMapper;
        setIgnoreMatchers(ignoreAntMatchers);
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        return ignoreMatchers.stream().anyMatch(matcher -> matcher.matches(request));
    }

    protected void authenticationFailed(AuthException exception, HttpServletResponse response) throws IOException{
        LoggerUtils.logException(logger, defaultLogLevel, exception);

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objMapper.writeValueAsString(new FailureDto(exception)));
        response.flushBuffer();
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        try{
            if(AuthUtils.getAuthenticatedUser().isEmpty()) {
                Optional.ofNullable( request.getHeader(HttpHeaders.AUTHORIZATION) )
                        .map(provider::getUser)
                        .ifPresent(AuthUtils::authenticate);
            }
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException ex) {
            authenticationFailed(AuthException.expiredToken(ex), response);
        } catch (JwtException ex) {
            authenticationFailed(AuthException.invalidToken(ex), response);
        }
    }

    public ObjectMapper getObjMapper() {
        return objMapper;
    }

    public void setObjMapper(ObjectMapper objMapper) {
        this.objMapper = objMapper;
    }

    public JwtProvider getProvider() {
        return provider;
    }

    public void setProvider(JwtProvider provider) {
        this.provider = provider;
    }

    public List<AntPathRequestMatcher> getIgnoreMatchers() {
        return ignoreMatchers;
    }

    public void setIgnoreMatchers(List<AntPathRequestMatcher> ignoreMatchers) {
        this.ignoreMatchers = ignoreMatchers;
    }

    public void setIgnoreMatchers(String... ignoreAntMatchers) {
        setIgnoreMatchers( Arrays.stream(ignoreAntMatchers)
                .map(AntPathRequestMatcher::new)
                .collect(Collectors.toList()) );
    }

    public Level getDefaultLogLevel() {
        return defaultLogLevel;
    }

    public void setDefaultLogLevel(Level defaultLogLevel) {
        this.defaultLogLevel = defaultLogLevel;
    }
}
