package park.brothers.runwith_back.Login;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import park.brothers.runwith_back.Runner.Runner;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    //login api
    @PostMapping("/api/runners/login")
    public ResponseEntity<Object> login(@RequestBody @Valid LoginForm form,
                                        BindingResult bindingResult,
                                        HttpServletResponse response) {

        if(bindingResult.hasErrors()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bindingResult.getAllErrors());
        }

        Runner loginRunner = loginService.login(form.getEmail(), form.getPassword());

        if(loginRunner == null){
            Map<String, String> error = new HashMap<>();
            error.put("message", "아이디 또는 비밀번호가 일치하지 않습니다.");

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        Cookie idCookie = new Cookie("runnerId", String.valueOf(loginRunner.getId()));
        idCookie.setPath("/");
        idCookie.setHttpOnly(true);
        response.addCookie(idCookie);

        return ResponseEntity.ok(loginRunner);
    }


    // logout api
    @PostMapping("/api/runners/logout")
    public ResponseEntity<String> logout(HttpServletResponse response){
        expireCookie(response);
        return ResponseEntity.ok("로그아웃 성공");
    }

    private void expireCookie(HttpServletResponse response){
        Cookie cookie = new Cookie("runnerId", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
