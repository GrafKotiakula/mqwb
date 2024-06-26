package knu.csc.ttp.qualificationwork.mqwb.entities.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum Role {
    ADMIN(Authority.values()), // ADMIN has all authorities,
    ANONYMOUS(Authority.ANONYMOUS_READ),
    USER(ANONYMOUS, Authority.CREATE, Authority.READ, Authority.UPDATE, Authority.DELETE),
    MODERATOR(USER,
            Authority.CREATE_USER, Authority.BLOCK_USER,
            Authority.MODIFY_COMPANY,
            Authority.MODIFY_GAME,
            Authority.DELETE_REVIEW);

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
        ANONYMOUS_READ,

        READ,
        CREATE,
        UPDATE,
        DELETE,

        CREATE_USER,
        UPDATE_USER,
        UPDATE_USER_ROLE,
        BLOCK_USER,
        DELETE_USER,

        MODIFY_COMPANY,
        MODIFY_GAME,

        DELETE_REVIEW,
        UPDATE_REVIEW;

        private final GrantedAuthority authority;

        Authority() {
            authority = new SimpleGrantedAuthority(name());
        }

        public GrantedAuthority getAuthority() {
            return authority;
        }
    }
}
