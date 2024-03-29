package knu.csc.ttp.qualificationwork.mqwb.entities.user.jpa;

import knu.csc.ttp.qualificationwork.mqwb.entities.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);
}
