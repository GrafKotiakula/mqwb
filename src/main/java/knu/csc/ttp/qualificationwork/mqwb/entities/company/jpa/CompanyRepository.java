package knu.csc.ttp.qualificationwork.mqwb.entities.company.jpa;

import knu.csc.ttp.qualificationwork.mqwb.entities.company.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CompanyRepository extends JpaRepository<Company, UUID> {
    Page<Company> findAllByNameContains(String name, Pageable pageable);
    Optional<Company> findByName(String name);
}
