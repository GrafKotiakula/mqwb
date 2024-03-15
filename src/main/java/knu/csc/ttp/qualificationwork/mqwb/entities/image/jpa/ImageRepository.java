package knu.csc.ttp.qualificationwork.mqwb.entities.image.jpa;

import knu.csc.ttp.qualificationwork.mqwb.entities.image.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ImageRepository extends JpaRepository<Image, UUID> {
}
