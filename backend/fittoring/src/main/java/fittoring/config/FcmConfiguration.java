package fittoring.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.io.ByteArrayInputStream;
import java.util.Base64;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile({"dev", "prod"})
@Configuration
public class FcmConfiguration {

    @Value("${fcm.service-account-key}")
    private String firebaseConfig;

    /**
     * FCM Admin SDK를 초기화합니다. 초기화 과정에서 Google 사용자 권한 인증 과정을 진행합니다.
     */
    @PostConstruct
    public void init() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                byte[] decodedKey = Base64.getDecoder().decode(firebaseConfig.trim());

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(new ByteArrayInputStream(decodedKey)))
                        .build();
                FirebaseApp.initializeApp(options);
            }
        } catch (Exception exception) {
            throw new RuntimeException("Firebase 초기화 실패: " + exception.getMessage());
        }
    }
}
