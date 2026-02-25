package park.brothers.runwith_back.common;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import park.brothers.runwith_back.external.Firebase.FirebaseAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final FirebaseAuthenticationFilter firebaseAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                //토큰 기반으로 인증할 것임으로 세션 기능은 끄기
                .csrf(AbstractHttpConfigurer::disable) // REST API이므로 CSRF 보안 비활성화
                .httpBasic(AbstractHttpConfigurer::disable) // 기본 로그인 비활성화
                .formLogin(AbstractHttpConfigurer::disable) // 폼 로그인 비활성화
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                //접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/public/**").permitAll() // 인증 없이 접근 가능한 경로
                        .anyRequest().authenticated() //이외의 요청은 모두 인증 필요
                )

                //FirebaseAuthenticationFilter에서 만든 인증서를 먼저 등록함(그 다음에 인증 단계로 넘어감)
                .addFilterBefore(firebaseAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                //인증하는 단계로, 인증에 실패하면 HttpStatusEntryPoint(401) 에러 발생
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                );

        return http.build();
    }
}