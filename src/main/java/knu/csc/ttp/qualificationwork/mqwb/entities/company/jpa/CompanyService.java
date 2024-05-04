package knu.csc.ttp.qualificationwork.mqwb.entities.company.jpa;

import knu.csc.ttp.qualificationwork.mqwb.abstractions.AbstractService;
import knu.csc.ttp.qualificationwork.mqwb.entities.company.Company;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CompanyService extends AbstractService<Company, CompanyRepository> {
    @Autowired
    public CompanyService(CompanyRepository repository) {
        super(repository);
    }

    public Page<Company> findAllByNameContains(String name, int page) {
        Pageable pageable = pageableOf(page);
        return Optional.ofNullable(name)
                .map(n -> repository.findAllByNameContainsIgnoreCase(n, pageable) )
                .orElse(Page.empty(pageable));
    }

    public Optional<Company> findByName(String name) {
        return Optional.ofNullable(name).flatMap(repository::findByName);
    }
}
