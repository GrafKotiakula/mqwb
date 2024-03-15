package knu.csc.ttp.qualificationwork.mqwb.entities.company.jackson;

import knu.csc.ttp.qualificationwork.mqwb.abstractions.jackson.deserialization.AbstractEntityDeserializer;
import knu.csc.ttp.qualificationwork.mqwb.entities.company.Company;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

public class CompanyDeserializer extends AbstractEntityDeserializer<Company> {
    @Autowired
    public CompanyDeserializer(ApplicationContext context) {
        super(context, Company.class);
    }
}
