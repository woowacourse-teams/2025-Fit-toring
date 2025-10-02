package fittoring.application.infra;

import fittoring.application.service.CodeGenerator;
import org.springframework.stereotype.Component;

@Component
public class CodeGeneratorStub implements CodeGenerator {

    @Override
    public String generate() {
        return "111111";
    }
}
