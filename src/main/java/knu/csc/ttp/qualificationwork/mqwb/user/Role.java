package knu.csc.ttp.qualificationwork.mqwb.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum Role {
    USER(Authority.CREATE, Authority.READ, Authority.UPDATE, Authority.DELETE),
    MODERATOR(USER, Authority.CREATE_USER, Authority.UPDATE_USER),
    ADMIN(Authority.values()); // ADMIN has all authorities

    private final Set<GrantedAuthority> authorities;

    Role(Authority... authorities) {
        this.authorities = Arrays.stream(authorities).map(Authority::getAuthority).collect(Collectors.toSet());
    }

    Role(Role base, Authority... authorities) {
        this(authorities);
        this.authorities.addAll(base.authorities);
    }

    public Set<GrantedAuthority> getAuthorities() {
        return Set.copyOf(authorities);
    }

    public enum Authority {
        READ,
        CREATE,
        UPDATE,
        DELETE,

        CREATE_USER,
        UPDATE_USER,
        UPDATE_USER_PASSWORD,
        DELETE_USER;

        private final GrantedAuthority authority;

        Authority() {
            authority = new SimpleGrantedAuthority(name());
        }

        public GrantedAuthority getAuthority() {
            return authority;
        }
    }
}
