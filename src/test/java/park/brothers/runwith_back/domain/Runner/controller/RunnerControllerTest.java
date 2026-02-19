package park.brothers.runwith_back.domain.Runner.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import park.brothers.runwith_back.domain.Runner.dto.Request.CreateRunnerRequestDto;
import park.brothers.runwith_back.domain.Runner.dto.Response.CreateRunnerResponseDto;
import park.brothers.runwith_back.domain.Runner.service.RunnerService;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class) //Mockito 사용 선언
class RunnerControllerTest {

    private MockMvc mockMvc;

    @Mock //가짜 서비스 객체
    private RunnerService runnerService;

    @InjectMocks //가짜 서비스 객체를 주입받을 실제 컨트롤러
    private RunnerController runnerController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(runnerController) //스프링 컨텍스트 없이 테스트 수행
                .alwaysDo(print()) // 모든 요청에 대해 로그 출력
                .build();
    }

    @Test
    @DisplayName("러너 저장 테스트 (201 Created)")
    void saveSuccess() throws Exception {
        //given
        //api request 생성
        CreateRunnerRequestDto createRunnerRequestDto = new CreateRunnerRequestDto(
              "test_runner_name",
              "test_runner_email",
              "test_runner_password"
        );
        //api response 생성
        CreateRunnerResponseDto createRunnerResponseDto = new CreateRunnerResponseDto(
                1L,
                "test_runner_name",
                "test_runner_email",
                "test_runner_imageLink"
        );

        String content = new ObjectMapper().writeValueAsString(createRunnerRequestDto);
        //가짜 request dto
        MockMultipartFile requestPart = new MockMultipartFile(
                "request",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                content.getBytes(StandardCharsets.UTF_8)
        );
        //가짜 이미지
        MockMultipartFile imagePart = new MockMultipartFile(
                "image",
                "profile.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "dummy_image_data".getBytes()
        );

        //when
        //서비스 응답 시, 올바른 응답값을 return해야함
        given(runnerService.checkDuplication(any(CreateRunnerRequestDto.class))).willReturn(false);
        given(runnerService.save(any(CreateRunnerRequestDto.class), any())).willReturn(createRunnerResponseDto);

        //then
        mockMvc.perform(multipart("/api/v1/runners") // POST 요청 URL
                        .file(requestPart) //JSON데이터 추가
                        .file(imagePart)   //image데이터 추가
                        .contentType(MediaType.MULTIPART_FORM_DATA) // 전체 Content-Type
                        .accept(MediaType.APPLICATION_JSON)) // JSON 응답을 기대함
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.runnerId").value(1L)) // runnerId 확인
                .andExpect(jsonPath("$.name").value("test_runner_name")) // 이름 확인
                .andExpect(jsonPath("$.email").value("test_runner_email"))
                .andExpect(jsonPath("$.imageLink").value("test_runner_imageLink"));

    }

    @Test
    @DisplayName("러너 중복 생성 실패 테스트 (409 Conflict)")
    void saveDuplication() throws Exception {
        // given
        // api request
        CreateRunnerRequestDto createRunnerRequestDto = new CreateRunnerRequestDto(
                "duplicate_runner_name",
                "duplicate_runner_email",
                "duplicate_runner_password"
        );

        String content = new ObjectMapper().writeValueAsString(createRunnerRequestDto);
        //가짜 request dto
        MockMultipartFile requestPart = new MockMultipartFile(
                "request",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                content.getBytes(StandardCharsets.UTF_8)
        );
        //가짜 이미지
        MockMultipartFile imagePart = new MockMultipartFile(
                "image",
                "profile.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "dummy_image_data".getBytes()
        );

        // 중복값 확인 서비스 수행 시 true로 반환해야 함
        given(runnerService.checkDuplication(any(CreateRunnerRequestDto.class))).willReturn(true);

        // when & then
        mockMvc.perform(multipart("/api/v1/runners")
                        .file(requestPart) //JSON데이터 추가
                        .file(imagePart)   //image데이터 추가
                        .contentType(MediaType.MULTIPART_FORM_DATA) // 전체 Content-Type
                        .accept(MediaType.APPLICATION_JSON)) // JSON 응답을 기대함
                .andExpect(status().isConflict()) //409코드 발생해야 함
                .andExpect(jsonPath("$.message").value("생성하고자 하는 데이터를 가진 러너들이 이미 존재합니다.")); //오류 메시지 일치 확인
    }
}