package knu.csc.ttp.qualificationwork.mqwb.controllers.authentication;

import com.fasterxml.jackson.databind.JsonNode;
import knu.csc.ttp.qualificationwork.mqwb.LoggerUtils;
import knu.csc.ttp.qualificationwork.mqwb.abstractions.controllers.AbstractController;
import knu.csc.ttp.qualificationwork.mqwb.config.security.JwtProvider;
import knu.csc.ttp.qualificationwork.mqwb.exceptions.client.AuthException;
import knu.csc.ttp.qualificationwork.mqwb.entities.user.User;
import knu.csc.ttp.qualificationwork.mqwb.entities.user.UserController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController extends AbstractController {
    private final AuthenticationManager authManager;
    private final JwtProvider provider;
    private final UserController userController;

    @Autowired
    public AuthController(ApplicationContext context) {
        super(context);
        this.authManager = context.getBean(AuthenticationManager.class);
        this.provider = context.getBean(JwtProvider.class);
        this.userController = context.getBean(UserController.class);
    }

    protected LoginResponseDto buildLoginResponseDto(User user) {
        String jwtToken = provider.createToken(user.getId());
        logger.log(defaultLogLvl,"{} is authenticated successfully", user);
        return new LoginResponseDto(user.getId(), jwtToken);
    }

    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody LoginRequestDto request){
        try {
            if(request.getUsername() == null || request.getPassword() == null){
                throw new BadCredentialsException("username or password is null");
            }
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
            User user = (User) authManager.authenticate(authToken).getPrincipal();

            return buildLoginResponseDto(user);
        } catch (AuthenticationException ex) {
            throw LoggerUtils.debugException(logger, AuthException.wrongUsernameOrPassword(ex));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<LoginResponseDto> signup(@RequestBody JsonNode node) {
        User user = userController.parseEntityOnCreate(node);
        userController.getValidator().validate(user);
        user = userController.getService().create(user);

        return ResponseEntity
                .created(userController.buildEntityUriFromCurrentController(user))
                .body(buildLoginResponseDto(user));
    }
}
