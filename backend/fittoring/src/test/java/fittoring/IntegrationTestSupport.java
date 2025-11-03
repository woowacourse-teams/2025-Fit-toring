package fittoring;

import fittoring.application.auth.service.JwtProvider;
import fittoring.application.image.service.PresignedUrlService;
import fittoring.application.mentoring.repository.MentoringPaginationHelper;
import fittoring.logging.JsonLogger;
import fittoring.util.DbCleaner;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public abstract class IntegrationTestSupport {

    @MockitoBean
    protected PresignedUrlService presignedUrlService;

    @MockitoBean
    protected JsonLogger jsonLogger;

    @MockitoBean
    protected MentoringPaginationHelper mph;

    @MockitoSpyBean
    protected JwtProvider jwtProvider;

    @Autowired
    private DbCleaner dbCleaner;

    @BeforeEach
    void setUp() {
        dbCleaner.clean();
    }
}
