package knu.csc.ttp.qualificationwork.mqwb.entities.company.jackson;

import knu.csc.ttp.qualificationwork.mqwb.abstractions.jackson.serialization.AbstractEntitySerializer;
import knu.csc.ttp.qualificationwork.mqwb.entities.company.Company;

public class CompanySerializer extends AbstractEntitySerializer<Company> {
    public CompanySerializer() {
        super(Company.class);
    }
}
