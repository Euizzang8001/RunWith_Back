package park.brothers.runwith_back.external.Firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@Component
public class FirebaseAuthenticationFilter extends OncePerRequestFilter { //한 요청당 한번 사용하도록 OncePerRequestFilter  를 상속받음

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {

        //헤더에 Authorization 헤더의 값 가져오기
        String authorizationHeader = request.getHeader("Authorization");

        //authorizationHeader가 있으면서 Bearer로 시작하면 올바른 토큰 형태임
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.replace("Bearer ", ""); //헤더값 중 토큰만 가져오기

            try {
                //토큰을 가지고 파이어베이스로부터 파이어베이스 토큰 가져오기
                FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
                String uid = decodedToken.getUid(); //파이어베이스토큰으로부터 uid가져오기

                // 스프링 시큐리티에서 사용할 로그인 인증서 생성(UsernamePasswordAuthenticationToken) -> 이미 firebase로 인증했으므로, 비밀번호(credentials)는 null로 설정
                //Collections.emptyList()는 권한을 나타냄 (일반적인 사용자는 권한 없음)
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(uid, null, Collections.emptyList());

                //부가 정보(요청 ip, 세션 id 등)을 함께 설정
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                //현재 요청을 보낸 사용자에 대한 인증을 저장함
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (FirebaseAuthException e) { //토큰 검증 실패 오류 (만료, 위조 등)
                log.error("파이어베이스 토큰 검증 실패 오류 : {}", e.getMessage());
                //SecurityContext를 비워두면, SecurityConfig에서 401 처리된다
                SecurityContextHolder.clearContext();
            } catch (Exception e) { //다른 오류라면, 오류 로그로 보내기
                log.error("인증 실패: {}", e.getMessage());
                SecurityContextHolder.clearContext(); //SecurityContext를 비워두면, SecurityConfig에서 401 처리된다
            }
        }

        //인증에 성공하면 controller로, 인증 실패하면 SecurityConfig로 이동
        filterChain.doFilter(request, response);
    }
}