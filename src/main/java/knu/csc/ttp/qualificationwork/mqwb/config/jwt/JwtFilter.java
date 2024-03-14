package knu.csc.ttp.qualificationwork.mqwb.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import knu.csc.ttp.qualificationwork.mqwb.LoggerUtils;
import knu.csc.ttp.qualificationwork.mqwb.controllers.exceptionresolver.FailureDto;
import knu.csc.ttp.qualificationwork.mqwb.exceptions.client.AuthException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class JwtFilter extends OncePerRequestFilter {
    private final Logger logger = LogManager.getLogger(getClass());
    private final ObjectMapper objMapper;
    private final JwtProvider provider;
    private final List<AntPathRequestMatcher> ignoreMatchers;

    @Autowired
    public JwtFilter(JwtProvider provider, ObjectMapper objMapper, String... ignoreAntMatchers) {
        this.provider = provider;
        this.objMapper = objMapper;
        ignoreMatchers = Arrays.stream(ignoreAntMatchers)
                .map(AntPathRequestMatcher::new)
                .collect(Collectors.toList());
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        return ignoreMatchers.stream().anyMatch(matcher -> matcher.matches(request));
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        AuthException exception;
        try{
            Authentication auth = provider.getAuthentication(request);
            SecurityContextHolder.getContext().setAuthentication(auth);
            filterChain.doFilter(request, response);
            return;
        } catch (ExpiredJwtException ex) {
            exception = AuthException.expiredToken(ex);
        } catch (JwtException ex) {
            exception = AuthException.invalidToken(ex);
        }
        LoggerUtils.debugException(logger, exception);

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objMapper.writeValueAsString(new FailureDto(exception)));
        response.flushBuffer();
    }
}
