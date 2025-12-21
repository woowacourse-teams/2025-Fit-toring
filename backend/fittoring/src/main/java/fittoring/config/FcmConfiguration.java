package fittoring.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import javax.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class FcmConfiguration {

    /**
     * FCM Admin SDK를 초기화합니다. 초기화 과정에서 Google 사용자 권한 인증 과정을 진행합니다.
     * <p>
     * firebase-service-key.json: Google 서비스 계정 키
     */
    @PostConstruct
    public void init() {
        try {
            ClassPathResource resource = new ClassPathResource("firebase-service-key.json");
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(resource.getInputStream()))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
        } catch (Exception exception) {
            throw new RuntimeException("Firebase 초기화 실패: " + exception.getMessage());
        }
    }
}
