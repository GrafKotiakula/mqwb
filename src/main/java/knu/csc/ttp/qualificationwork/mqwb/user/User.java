package knu.csc.ttp.qualificationwork.mqwb.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import knu.csc.ttp.qualificationwork.mqwb.abstractions.AbstractEntity;
import knu.csc.ttp.qualificationwork.mqwb.abstractions.validation.ValidationGroup;
import knu.csc.ttp.qualificationwork.mqwb.user.jackson.UserDeserializer;
import knu.csc.ttp.qualificationwork.mqwb.user.jackson.UserSerializer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Optional;

@Entity
@Table(name = "users")
@JsonSerialize(using = UserSerializer.class)
@JsonDeserialize(using = UserDeserializer.class)
public class User extends AbstractEntity implements UserDetails {
    @Column(name = "username", length = 50, nullable = false, unique = true)
    @NotBlank @Pattern(regexp = "^[a-zA-Z0-9-_.]*$", message = "wrongUsernameFormat")
    private String username;

    @Column(name = "password", length = 100, nullable = false)
    @ValidationGroup(exclude = UserValidator.UPDATE)
    @Size(min = 5) @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).*$", message = "wrongPasswordFormat")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.ENABLED;

    @Column(name = "role", nullable = false)
    private Role role = Role.USER;

    @Column(name = "created", nullable = false)
    private ZonedDateTime created = ZonedDateTime.now();

    @Column(name = "expiration")
    private ZonedDateTime expiration;

    @Column(name = "credentials_expiration")
    private ZonedDateTime credentialsExpiration;

    @Override
    @JsonProperty
    public String getUsername() {
        return username;
    }

    @JsonProperty
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @JsonProperty
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @JsonProperty
    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @JsonProperty
    public ZonedDateTime getCreated() {
        return created;
    }

    public void setCreated(ZonedDateTime created) {
        this.created = created;
    }

    public ZonedDateTime getExpiration() {
        return expiration;
    }

    public void setExpiration(ZonedDateTime expiration) {
        this.expiration = expiration;
    }

    public ZonedDateTime getCredentialsExpiration() {
        return credentialsExpiration;
    }

    public void setCredentialsExpiration(ZonedDateTime credentialsExpiration) {
        this.credentialsExpiration = credentialsExpiration;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
    }

    @Override
    public boolean isAccountNonExpired() {
        return Optional.ofNullable(expiration)
                .map( exp -> exp.isAfter(ZonedDateTime.now()) )
                .orElse(true);
    }

    @Override
    public boolean isAccountNonLocked() {
        return status != Status.LOCKED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return Optional.ofNullable(credentialsExpiration)
                .map( exp -> exp.isAfter(ZonedDateTime.now()) )
                .orElse(true);
    }

    @Override
    public boolean isEnabled() {
        return  status == Status.ENABLED;
    }
}
