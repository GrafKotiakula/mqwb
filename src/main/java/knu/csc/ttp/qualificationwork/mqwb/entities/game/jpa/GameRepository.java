package knu.csc.ttp.qualificationwork.mqwb.entities.game.jpa;

import knu.csc.ttp.qualificationwork.mqwb.entities.game.Game;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface GameRepository extends JpaRepository<Game, UUID> {
    Page<Game> findAllByNameContains(String name, Pageable pageable);
    Optional<Game> findByName(String name);
}
