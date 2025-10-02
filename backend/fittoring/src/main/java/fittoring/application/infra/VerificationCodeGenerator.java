package fittoring.application.infra;

import fittoring.application.business.service.CodeGenerator;
import java.security.SecureRandom;
import org.springframework.stereotype.Component;

@Component
public class VerificationCodeGenerator implements CodeGenerator {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Override
    public String generate() {
        int code = SECURE_RANDOM.nextInt(1_000_000);
        return String.format("%06d", code);
    }
}
