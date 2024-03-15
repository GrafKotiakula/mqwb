package knu.csc.ttp.qualificationwork.mqwb.entities.company;

import knu.csc.ttp.qualificationwork.mqwb.abstractions.validation.AbstractEntityValidator;
import knu.csc.ttp.qualificationwork.mqwb.entities.company.jpa.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CompanyValidator extends AbstractEntityValidator<Company> {
    protected static final String NAME_FIELD_NAME = "name";

    private final CompanyService service;

    @Autowired
    public CompanyValidator(CompanyService service) {
        super(Company.class);
        this.service = service;
    }

    @Override
    public void validate(Company company, String validationGroup) {
        super.validate(company, validationGroup);
        if(validationGroup == null || validationGroup.equals(CREATE) || validationGroup.equals(UPDATE)) {
            validateUniqueness(company, service.findByName(company.getName()), defaultLogLvl, NAME_FIELD_NAME);
        }
    }
}
