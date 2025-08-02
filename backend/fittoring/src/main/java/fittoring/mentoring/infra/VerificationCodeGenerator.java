package fittoring.mentoring.infra;

import fittoring.mentoring.business.service.CodeGenerator;
import java.security.SecureRandom;
import org.springframework.stereotype.Component;

@Component
public class VerificationCodeGenerator implements CodeGenerator {

    private static final SecureRandom SECURE_RAMDOM = new SecureRandom();

    @Override
    public String generate() {
        int code = SECURE_RAMDOM.nextInt(1_000_000);
        return String.format("%06d", code);
    }
}
