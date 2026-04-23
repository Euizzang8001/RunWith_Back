package park.brothers.runwith_back.external.Firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

@Configuration //Config 파일임을 선언 -> 앱 시작 시 파이어베이스와 연결
public class FirebaseConfig {

    @Value("${firebase.base.key}") // application.yml에서 읽어옴
    private String firebaseBaseKey;

    @Bean
    public FirebaseApp initializeFirebase() throws IOException {
        //firebaseBaseKey를 읽어와, Base64로 인코딩한 후, 파이어베이스에 서버임을 인증하는데 사용
        String base64EncodedServiceAccountKey = firebaseBaseKey;

        InputStream credentialsStream = new ByteArrayInputStream(Base64.getDecoder().decode(base64EncodedServiceAccountKey));

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(credentialsStream))
                .build();

        //파이어베이스의 앱이 없으면 시작하고, 있으면 인스턴스 가져오기
        if (FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.initializeApp(options);
        } else {
            return FirebaseApp.getInstance();
        }

    }
}