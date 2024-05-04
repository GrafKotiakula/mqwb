package knu.csc.ttp.qualificationwork.mqwb.entities.company;

import knu.csc.ttp.qualificationwork.mqwb.abstractions.controllers.AbstractCrudController;
import knu.csc.ttp.qualificationwork.mqwb.entities.company.jpa.CompanyService;
import knu.csc.ttp.qualificationwork.mqwb.entities.user.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/data/company")
public class CompanyController extends AbstractCrudController<Company, CompanyService, CompanyValidator> {
    protected GrantedAuthority findByNameAuthority = Role.Authority.ANONYMOUS_READ.getAuthority();

    @Autowired
    public CompanyController(ApplicationContext context) {
        super(context);
        createAuthority = Role.Authority.MODIFY_COMPANY.getAuthority();
        updateAuthority = Role.Authority.MODIFY_COMPANY.getAuthority();
        deleteAuthority = Role.Authority.MODIFY_COMPANY.getAuthority();
    }

    @GetMapping("/find-by/name")
    public Page<Company> getAllByName(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                      @RequestParam("filter") String filter) {
        checkAuthority(findByNameAuthority);
        return service.findAllByNameContains(filter, page);
    }

    @GetMapping("/find-or-create")
    public ResponseEntity<Company> findOrCreate(@RequestParam(value = "name") String name) {
        checkAuthority(createAuthority);

        Optional<Company> optCompany = service.findByName(name);
        if(optCompany.isPresent()) {
            return ResponseEntity.ok(optCompany.get());
        } else {
            Company company = new Company();
            company.setName(name);

            validator.validate(company, CompanyValidator.NO_NAME_UNIQUENESS_CHECK); // uniqueness is already checked
            company = service.create(company);

            return ResponseEntity
                    .created(buildEntityUriFromCurrentController(company))
                    .body(company);
        }
    }
}
