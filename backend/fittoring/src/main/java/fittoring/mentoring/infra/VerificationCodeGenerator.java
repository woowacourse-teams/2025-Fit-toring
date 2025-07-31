package fittoring.mentoring.infra;

import fittoring.mentoring.business.service.CodeGenerator;
import java.security.SecureRandom;
import org.springframework.stereotype.Component;

@Component
public class VerificationCodeGenerator implements CodeGenerator {

    private static final SecureRandom secureRandom = new SecureRandom();

    @Override
    public String generate() {
        int code = secureRandom.nextInt(1_000_000);
        return String.format("%06d", code);
    }
}
