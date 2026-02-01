package park.brothers.runwith_back.Login;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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
                                        HttpServletRequest request){

        if(bindingResult.hasErrors()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bindingResult.getAllErrors());
        }

        Runner loginRunner = loginService.login(form.getEmail(), form.getPassword());

        if(loginRunner == null){
            Map<String, String> error = new HashMap<>();
            error.put("message", "아이디 또는 비밀번호가 일치하지 않습니다.");

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        HttpSession session = request.getSession();
        session.setAttribute("loginRunner", loginRunner);

        return ResponseEntity.ok(loginRunner);
    }


    // logout api
    @PostMapping("/api/runners/logout")
    public ResponseEntity<String> logout(HttpServletRequest request){
        HttpSession session = request.getSession(false);
        if(session != null){
            session.invalidate();
        }
        return ResponseEntity.ok("로그아웃 성공");
    }
}
