package fittoring.mentoring.infra;

import fittoring.mentoring.business.service.CodeGenerator;
import org.springframework.stereotype.Component;

@Component
public class CodeGeneratorStub implements CodeGenerator {

    @Override
    public String generate() {
        return "111111";
    }
}
