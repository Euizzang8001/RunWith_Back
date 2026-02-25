package park.brothers.runwith_back.external.Firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@Configuration //Config 파일임을 선언 -> 앱 시작 시 파이어베이스와 연결
public class FirebaseConfig {

    @Bean
    public FirebaseApp initializeFirebase() throws IOException {
        System.out.println("new ClassPathResource(\"firebaseKey.json\") = " + new ClassPathResource("firebaseKey.json"));
        //firebase-key.json을 읽어와, 파이어베이스에 서버임을 인증
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(new ClassPathResource("firebaseKey.json").getInputStream()))
                .build();

        //파이어베이스의 앱이 없으면 시작하고, 있으면 인스턴스 가져오기
        if (FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.initializeApp(options);
        } else {
            return FirebaseApp.getInstance();
        }

    }
}