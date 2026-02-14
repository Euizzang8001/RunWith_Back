package park.brothers.runwith_back.domain.Login.controller;

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
import park.brothers.runwith_back.common.CommonMessage;
import park.brothers.runwith_back.common.Response.ValidationErrorUtils;
import park.brothers.runwith_back.domain.Login.dto.Request.LoginRequestDto;
import park.brothers.runwith_back.domain.Login.dto.Response.LoginResponseDto;
import park.brothers.runwith_back.domain.Login.service.LoginService;

@Slf4j
@RestController
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    //login api
    @PostMapping("/api/v1/runners/login")
    public ResponseEntity<Object> login(@RequestBody @Valid LoginRequestDto loginRequestDto,
                                        BindingResult bindingResult,
                                        HttpServletResponse response) {

        if (bindingResult.hasErrors()) {
            return ValidationErrorUtils.handleValidationErrors(bindingResult);
        }

        //로그인
        Long loginRunnerId = loginService.login(loginRequestDto);

        //로그인 성공 여부 파악
        if(loginRunnerId == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new CommonMessage("아이디 또는 비밀번호가 일치하지 않습니다."));
        }

        //Http 쿠키 설정
        Cookie idCookie = new Cookie("runnerId", String.valueOf(loginRunnerId));
        idCookie.setPath("/");
        idCookie.setHttpOnly(true);
        response.addCookie(idCookie);

        return ResponseEntity.status(HttpStatus.OK).body(new LoginResponseDto(loginRunnerId));
    }


    // logout api
    @PostMapping("/api/v1/runners/logout")
    public ResponseEntity<Object> logout(HttpServletResponse response){
        expireCookie(response);
        return ResponseEntity.status(HttpStatus.OK).body(new CommonMessage("로그아웃 성공"));

    }

    private void expireCookie(HttpServletResponse response){
        Cookie cookie = new Cookie("runnerId", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
