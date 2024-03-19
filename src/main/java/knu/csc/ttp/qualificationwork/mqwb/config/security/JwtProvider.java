package knu.csc.ttp.qualificationwork.mqwb.config.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.WeakKeyException;
import knu.csc.ttp.qualificationwork.mqwb.Constants;
import knu.csc.ttp.qualificationwork.mqwb.LoggerUtils;
import knu.csc.ttp.qualificationwork.mqwb.entities.user.User;
import knu.csc.ttp.qualificationwork.mqwb.entities.user.jpa.UserService;
import knu.csc.ttp.qualificationwork.mqwb.exceptions.server.InternalServerErrorException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtProvider  {
    public static final String BEARER_TOKEN_PREFIX = "Bearer ";

    protected final Logger logger = LoggerUtils.getNamedLogger(Constants.securityLoggerName, getClass());
    protected Level defaultLogLevel = Constants.defaultControllerLogLvl;
    private final SecretKey secretKey;
    private final Long tokenExpirationMillis;
    private final UserService service;

    @Autowired
    public JwtProvider(@Value("${jwt.token.expired}") Long tokenExpirationMillis,
                       @Value("${jwt.token.secret}") String secret,
                       UserService service) {
        this.tokenExpirationMillis = tokenExpirationMillis;
        this.service = service;
        this.secretKey = generateSecretKeyFromString(secret);
    }

    private SecretKey generateSecretKeyFromString(String secret) {
        try{
            return Keys.hmacShaKeyFor( Base64.getEncoder().encode(secret.getBytes(StandardCharsets.UTF_8)) );
        } catch (WeakKeyException ex) {
            throw LoggerUtils.fatalException(logger, InternalServerErrorException.tooWeakKey(ex));
        }
    }

    public String createToken(UUID id){
        Date enabled = new Date();
        Date expired = new Date(enabled.getTime() + tokenExpirationMillis);

        return createToken(id, enabled, expired);
    }

    public String createToken(UUID id, Date enabled, Date expired){
        String token = Jwts.builder()
                .claims().subject(id.toString()).and()
                .issuedAt(new Date())
                .notBefore(enabled)
                .expiration(expired)
                .signWith(secretKey)
                .compact();

        return BEARER_TOKEN_PREFIX + token;
    }

    private UUID extractId(String bearerToken) throws JwtException {
        if(bearerToken == null){
            throw new JwtException("token is not provided");
        }
        if(!bearerToken.startsWith(BEARER_TOKEN_PREFIX)){
            throw new MalformedJwtException("token is not recognized");
        }
        String token = bearerToken.substring(BEARER_TOKEN_PREFIX.length());

        try {
            return UUID.fromString( Jwts.parser().verifyWith(secretKey).build()
                            .parseSignedClaims(token).getPayload().getSubject() );
        } catch (IllegalArgumentException ex) {
            throw new MalformedJwtException("Cannot extract uuid", ex);
        }
    }

    public User getUser(String token) throws JwtException{
        UUID id = extractId(token);
        return service.findByIdOrThrow(id);
    }

    public Level getDefaultLogLevel() {
        return defaultLogLevel;
    }

    public void setDefaultLogLevel(Level defaultLogLevel) {
        this.defaultLogLevel = defaultLogLevel;
    }
}
