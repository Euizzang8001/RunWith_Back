package park.brothers.runwith_back.domain.Runner.controller;

import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.annotation.AuthenticationPrincipal;


import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import park.brothers.runwith_back.domain.Runner.dto.Request.CreateRunnerRequestDto;
import park.brothers.runwith_back.domain.Runner.dto.Response.CreateRunnerResponseDto;
import park.brothers.runwith_back.domain.Runner.service.RunnerService;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
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
        mockMvc = MockMvcBuilders.standaloneSetup(runnerController)
                .setCustomArgumentResolvers(new HandlerMethodArgumentResolver() {
                    @Override
                    public boolean supportsParameter(@NonNull MethodParameter parameter) {
                        return parameter.hasParameterAnnotation(AuthenticationPrincipal.class);
                    }

                    @Override
                    public Object resolveArgument(@NonNull MethodParameter parameter, ModelAndViewContainer mavContainer, @NonNull NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
                        return "test_runner_uid";
                    }
                })
                .alwaysDo(print())
                .build();
    }

    @Test
    @DisplayName("러너 저장 테스트 (201 Created)")
    void saveSuccess() throws Exception {
        //given
        //api request 생성
        CreateRunnerRequestDto createRunnerRequestDto = new CreateRunnerRequestDto(
              "test_runner_name"
        );
        //api response 생성
        CreateRunnerResponseDto createRunnerResponseDto = new CreateRunnerResponseDto(
                "test_runner_name",
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
        given(runnerService.save(anyString(), any(CreateRunnerRequestDto.class), any())).willReturn(createRunnerResponseDto);

        //then
        mockMvc.perform(multipart("/api/v1/runners") // POST 요청 URL
                        .file(requestPart) //JSON데이터 추가
                        .file(imagePart)   //image데이터 추가
                        .with(csrf())
                        .contentType(MediaType.MULTIPART_FORM_DATA) // 전체 Content-Type
                        .accept(MediaType.APPLICATION_JSON)) // JSON 응답을 기대함
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.runnerName").value("test_runner_name")) // 이름 확인
                .andExpect(jsonPath("$.runnerImageLink").value("test_runner_imageLink"));

    }
}