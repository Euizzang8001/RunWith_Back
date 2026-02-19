package park.brothers.runwith_back.domain.Login.controller;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import park.brothers.runwith_back.domain.Login.dto.Request.LoginRequestDto;
import park.brothers.runwith_back.domain.Login.service.LoginService;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class LoginControllerTest {

    private MockMvc mockMvc;

    @Mock
    private LoginService loginService;

    @InjectMocks
    private LoginController loginController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(loginController) //스프링 컨텍스트 없이 테스트 수행
                .alwaysDo(print()) // 모든 요청에 대해 로그 출력
                .build();
    }

    @Test
    @DisplayName("로그인 성공 컨트롤러 테스트")
    void loginSuccess() throws Exception {
        //given
        //로그인 requestdto 생성
        LoginRequestDto loginRequestDto = new LoginRequestDto(
                "test_email",
                "test_password"
        );
        //servive 가정 선언
        given(loginService.login(any(LoginRequestDto.class))).willReturn(1L);

        //when & then
        String content = new ObjectMapper().writeValueAsString(loginRequestDto);

        mockMvc.perform(post("/api/v1/runners/login") // POST 요청 URL
                        .contentType(MediaType.APPLICATION_JSON) // 요청 타입 확인
                        .content(content)) // Body에 JSON 문자열 담기
                .andExpect(status().isOk()) //
                .andExpect(jsonPath("$.runnerId").value(1L)) // runnerId 확인
                .andExpect(cookie().value("runnerId", "1")); //쿠키값 테스트

    }

    @Test
    @DisplayName("로그인 실패 컨트롤러 테스트")
    void loginFail() throws Exception {
        //given
        //로그인 requestdto 생성
        LoginRequestDto loginRequestDto = new LoginRequestDto(
                "test_email",
                "test_password"
        );
        //servive 가정 선언
        given(loginService.login(any(LoginRequestDto.class))).willReturn(null);

        //when & then
        String content = new ObjectMapper().writeValueAsString(loginRequestDto);

        mockMvc.perform(post("/api/v1/runners/login") // POST 요청 URL
                        .contentType(MediaType.APPLICATION_JSON) // 요청 타입 확인
                        .content(content)) // Body에 JSON 문자열 담기
                .andExpect(status().isUnauthorized()) //
                .andExpect(jsonPath("$.message").value("아이디 또는 비밀번호가 일치하지 않습니다.")); // runnerId 확인

    }

    @Test
    @DisplayName("로그아웃 테스트")
    void logout() throws Exception {
        //given
        //함께 보낼 쿠키 선언
        Cookie requestCookie = new Cookie("runnerId", "1");

        //when & then
        mockMvc.perform(post("/api/v1/runners/logout")
                .cookie(requestCookie)) //요청에 쿠키 함께 보내기
                .andExpect(status().isOk()) //스테이터스 확인
                .andExpect(cookie().maxAge("runnerId", 0)) //쿠키 age가 0인지 확인
                .andExpect(jsonPath("$.message").value("로그아웃 성공")); //메시지 확인




    }
}