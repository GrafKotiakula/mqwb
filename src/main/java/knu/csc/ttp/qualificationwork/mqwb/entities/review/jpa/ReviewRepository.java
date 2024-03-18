package knu.csc.ttp.qualificationwork.mqwb.entities.review.jpa;

import knu.csc.ttp.qualificationwork.mqwb.entities.review.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
}
