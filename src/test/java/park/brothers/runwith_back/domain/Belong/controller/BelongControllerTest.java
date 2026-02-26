package park.brothers.runwith_back.domain.Belong.controller;

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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import park.brothers.runwith_back.domain.Belong.dto.Request.ChangeLeaderRequestDto;
import park.brothers.runwith_back.domain.Belong.dto.Request.CreateBelongRequestDto;
import park.brothers.runwith_back.domain.Belong.dto.Response.CreateBelongResponseDto;
import park.brothers.runwith_back.domain.Belong.service.BelongService;
import park.brothers.runwith_back.domain.Group.dto.Response.GetGroupResponseDto;
import park.brothers.runwith_back.domain.Runner.dto.Response.GetRunnerResponseDto;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@ExtendWith(MockitoExtension.class)
class BelongControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BelongService belongService;

    @InjectMocks
    private BelongController belongController;


    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(belongController)
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
    @DisplayName("belong 저장 컨트롤러 성공 테스트")
    void save() throws Exception {
        //given
        UUID groupUUID = UUID.randomUUID();
        String runnerStrId = "test_runner";
        String groupStrId = groupUUID.toString();
        String belongNickname = "test_nickname";

        CreateBelongRequestDto createBelongRequestDto = new CreateBelongRequestDto(
                groupStrId,
                belongNickname
        );

        UUID belongUUID = UUID.randomUUID();
        String belongStrId = belongUUID.toString();
        CreateBelongResponseDto createBelongResponseDto = new CreateBelongResponseDto(
                belongStrId,
                groupStrId,
                belongNickname,
                false
        );

        given(belongService.joinGroup(anyString(), any(CreateBelongRequestDto.class))).willReturn(createBelongResponseDto);

        //when & then
        String content = new ObjectMapper().writeValueAsString(createBelongRequestDto);

        mockMvc.perform(post("/api/v1/belongs") // POST 요청 URL
                        .contentType(MediaType.APPLICATION_JSON) // 요청 타입 확인
                        .content(content) // Body에 JSON 문자열 담기
                        .with(csrf()))
                .andExpect(status().isCreated()) //
                .andExpect(jsonPath("$.belongId").value(belongStrId)) // id 확인
                .andExpect(jsonPath("$.belongNickname").value(belongNickname))
                .andExpect(jsonPath("$.belongIsLeader").value(false));
    }

    @Test
    @DisplayName("그룹 탈퇴 성공 컨트롤러 테스트")
    void leave() throws Exception {
        //given
        UUID groupUUID = UUID.randomUUID();
        String groupStrId = groupUUID.toString();

        //when & then

        mockMvc.perform(delete("/api/v1/belongs/{groupId}", groupStrId) // POST 요청 URL
                        .contentType(MediaType.APPLICATION_JSON)) // 요청 타입 확인
                .andExpect(status().isOk()) //
                .andExpect(jsonPath("$.message").value("그룹에서 성공적으로 탈퇴되었습니다."));
    }

    @Test
    @DisplayName("특정 러너가 가입한 모든 그룹 찾기")
    void getAllGroupsRunnerJoin() throws Exception {
        //given
        String runnerStrId = "test_runner";

        UUID group1UUID = UUID.randomUUID();
        UUID group2UUID = UUID.randomUUID();

        GetGroupResponseDto getGroupResponseDto1 = new GetGroupResponseDto(
                group1UUID.toString(),
                "test_group1",
                "test_group1_description",
                "test_group1_imageLink"
        );
        GetGroupResponseDto getGroupResponseDto2 = new GetGroupResponseDto(
                group2UUID.toString(),
                "test_group2",
                "test_group2_description",
                "test_group2_imageLink"
        );

        given(belongService.getAllGroupsRunnerJoin(anyString())).willReturn(List.of(getGroupResponseDto1, getGroupResponseDto2));

        //when & then
        mockMvc.perform(get("/api/v1/belongs/runnerId={runnerId}", runnerStrId)) // get요청
                .andExpect(status().isOk()) //
                .andExpect(jsonPath("$", hasSize(2))) //전체 길이가 2인지 확인
                .andExpect(jsonPath("$[0].groupId").value(group1UUID.toString())) // groupId 확인
                .andExpect(jsonPath("$[0].groupName").value("test_group1")) // 이름 확인
                .andExpect(jsonPath("$[0].groupDescription").value("test_group1_description"))
                .andExpect(jsonPath("$[0].groupImageLink").value("test_group1_imageLink"))
                .andExpect(jsonPath("$[1].groupId").value(group2UUID.toString())) // groupId 확인
                .andExpect(jsonPath("$[1].groupName").value("test_group2")) // 이름 확인
                .andExpect(jsonPath("$[1].groupDescription").value("test_group2_description"))
                .andExpect(jsonPath("$[1].groupImageLink").value("test_group2_imageLink"));

    }

    @Test
    @DisplayName("특정 그룹에 속한 러너들 조회 성공 컨트롤러 테스트")
    void getAllRunnersInGroup() throws Exception {
        //given
        UUID groupUUID = UUID.randomUUID();
        String runner1Id = "test_runner1";
        String runner2Id = "test_runner2";

        GetRunnerResponseDto getRunnerResponseDto1 =  new GetRunnerResponseDto(
                runner1Id,
                "test_runner1",
                "test_runner1_imageLink"
        );
        GetRunnerResponseDto getRunnerResponseDto2 =  new GetRunnerResponseDto(
                runner2Id,
                "test_runner2",
                "test_runner2_imageLink"
        );

        given(belongService.getAllRunnersInGroup(anyString())).willReturn(List.of(getRunnerResponseDto1, getRunnerResponseDto2));

        //when & then
        mockMvc.perform(get("/api/v1/belongs/groupId={groupId}", groupUUID.toString())) // get요청
                .andExpect(status().isOk()) //
                .andExpect(jsonPath("$", hasSize(2))) //전체 길이가 2인지 확인
                .andExpect(jsonPath("$[0].runnerId").value(runner1Id)) // groupId 확인
                .andExpect(jsonPath("$[0].runnerName").value("test_runner1")) // 이름 확인
                .andExpect(jsonPath("$[0].runnerImageLink").value("test_runner1_imageLink"))
                .andExpect(jsonPath("$[1].runnerId").value(runner2Id)) // groupId 확인
                .andExpect(jsonPath("$[1].runnerName").value("test_runner2")) // 이름 확인
                .andExpect(jsonPath("$[1].runnerImageLink").value("test_runner2_imageLink"));



    }

    @Test
    @DisplayName("그룹 리더 변경 성공 컨트롤러 테스트")
    void changeLeader() throws Exception {
        //given
        ChangeLeaderRequestDto changeLeaderRequestDto = new ChangeLeaderRequestDto(
                "newLeaderRunnerId",
                UUID.randomUUID().toString()
        );
        //when & then
        String content = new ObjectMapper().writeValueAsString(changeLeaderRequestDto);

        mockMvc.perform(patch("/api/v1/belongs/leader") // POST 요청 URL
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)// 요청 타입 확인
                        .with(csrf()))
                .andExpect(status().isAccepted()) //
                .andExpect(jsonPath("$.message").value("해당 그룹의 리더가 성공적으로 변경되었습니다."));
    }
}