package knu.csc.ttp.qualificationwork.mqwb.entities.user.jpa;

import knu.csc.ttp.qualificationwork.mqwb.abstractions.AbstractService;
import knu.csc.ttp.qualificationwork.mqwb.entities.user.User;
import org.apache.logging.log4j.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService extends AbstractService<User, UserRepository> implements UserDetailsService {
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository repository, PasswordEncoder passwordEncoder) {
        super(repository);
        this.passwordEncoder = passwordEncoder;
    }

    protected void encodePassword(User user) {
        if(user.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
    }

    @Override
    public User create(User user, Level logLevel) {
        encodePassword(user);
        return super.create(user, logLevel);
    }

    public User updatePassword(User user) {
        return updatePassword(user, defaultUpdateLogLvl);
    }

    public User updatePassword(User user, Level logLevel) {
        encodePassword(user);
        return update(user, logLevel);
    }

    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(username).flatMap(repository::findByUsername);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findByUsername(username).orElseThrow(() -> {
            String message = String.format("%s[username=%s] not found", User.class.getSimpleName(), username);
            logger.debug(message);
            return new UsernameNotFoundException(message);
        });
    }


}
