package knu.csc.ttp.qualificationwork.mqwb.entities.review.jpa;

import knu.csc.ttp.qualificationwork.mqwb.entities.game.Game;
import knu.csc.ttp.qualificationwork.mqwb.entities.review.Review;
import knu.csc.ttp.qualificationwork.mqwb.entities.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
    Page<Review> getAllByGame(Game game, Pageable pageable);
    Page<Review> getAllByUser(User user, Pageable pageable);
    Optional<Review> getByGameAndUser(Game game, User user);
}
