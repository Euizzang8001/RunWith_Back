package park.brothers.runwith_back.domain.Login.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import park.brothers.runwith_back.common.Exceptions.ResourceNotFoundException;
import park.brothers.runwith_back.domain.Login.dto.Request.LoginRequestDto;
import park.brothers.runwith_back.domain.Login.dto.Response.LoginResponseDto;
import park.brothers.runwith_back.domain.Runner.entity.Runner;
import park.brothers.runwith_back.domain.Runner.repository.RunnerRepository;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class Version1LoginServiceTest {

    @Mock
    private RunnerRepository runnerRepository;

    @InjectMocks
    private Version1LoginService loginService;

    @Test
    @DisplayName("로그인 성공 서비스 테스트")
    void loginSuccess() {
        //given
        //request 선언
        LoginRequestDto loginRequestDto = new LoginRequestDto(
                "test_email",
                "test_password"
        );

        //레퍼지토리 메서드 수행 시 반환할 값 선언
        Runner runner = new Runner();
        UUID fakeRunnerUUID = UUID.randomUUID();
        runner.setId(fakeRunnerUUID);
        runner.setEmail("test_email");
        runner.setPassword("test_password");
        runner.setName("test_name");

        LoginResponseDto loginResponseDto =  new LoginResponseDto(
                fakeRunnerUUID.toString(),
                "test_name"
        );

        //이메일로 찾을 시, 잘 찾아지는지 확인
        given(runnerRepository.findByEmail(anyString())).willReturn(Optional.of(runner));

        //then
        assertThat(loginService.login(loginRequestDto)).isEqualTo(loginResponseDto); //response가 잘 반환되는지 확인
    }

    @Test
    @DisplayName("로그인 실패 서비스 테스트")
    void loginFail() {
        //given
        //request 선언
        LoginRequestDto loginRequestDto = new LoginRequestDto(
                "test_email",
                "test_password"
        );

        //이메일로 찾을 시, 찾아지는 값이 없도록 함
        given(runnerRepository.findByEmail(anyString())).willReturn(Optional.empty());

        //then
        assertThrows(ResourceNotFoundException.class, () -> loginService.login(loginRequestDto)); //null값이 반환되는지 확인
    }
}