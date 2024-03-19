package knu.csc.ttp.qualificationwork.mqwb.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import knu.csc.ttp.qualificationwork.mqwb.AuthUtils;
import knu.csc.ttp.qualificationwork.mqwb.Constants;
import knu.csc.ttp.qualificationwork.mqwb.LoggerUtils;
import knu.csc.ttp.qualificationwork.mqwb.entities.user.Role;
import knu.csc.ttp.qualificationwork.mqwb.entities.user.Status;
import knu.csc.ttp.qualificationwork.mqwb.entities.user.User;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class AnonymousFilter extends OncePerRequestFilter {
    protected final Logger logger = LoggerUtils.getNamedLogger(Constants.securityLoggerName, getClass());
    protected List<HttpMethod> allowedMethods;

    public AnonymousFilter(HttpMethod... allowedMethods) {
        this.allowedMethods = List.of(allowedMethods);
        logger.trace( "Anonymous filter allows methods: {}",
                this.allowedMethods.stream().map(HttpMethod::toString).collect(Collectors.joining(", ")) );
    }

    protected boolean methodIsAllowed(HttpServletRequest request) {
        return allowedMethods.stream().anyMatch( m -> m.matches(request.getMethod()) );
    }

    protected boolean noAuthorization(HttpServletRequest request) {
        return request.getHeader(HttpHeaders.AUTHORIZATION) == null && AuthUtils.getAuthenticatedUser().isEmpty();
    }

    protected User buildAnonymousUser() {
        User user = new User();

        user.setId(null);
        user.setUsername(null);
        user.setPassword(null);
        user.setRole(Role.ANONYMOUS);
        user.setStatus(Status.ENABLED);

        user.setCreated(null);
        user.setExpiration(null);
        user.setCredentialsExpiration(null);
        user.setImage(null);

        return user;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        if( methodIsAllowed(request) && noAuthorization(request)) {
            AuthUtils.authenticate(buildAnonymousUser());
        }
        filterChain.doFilter(request, response);
    }

    public List<HttpMethod> getAllowedMethods() {
        return allowedMethods;
    }

    public void setAllowedMethods(List<HttpMethod> allowedMethods) {
        this.allowedMethods = allowedMethods;
    }
}
