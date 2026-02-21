package park.brothers.runwith_back.domain.Group.controller;

import org.jspecify.annotations.NonNull;
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
import org.springframework.web.multipart.MultipartFile;
import park.brothers.runwith_back.domain.Group.dto.Request.CreateGroupRequestDto;
import park.brothers.runwith_back.domain.Group.dto.Request.DeleteGroupRequestDto;
import park.brothers.runwith_back.domain.Group.dto.Request.ReviseGroupRequestDto;
import park.brothers.runwith_back.domain.Group.dto.Response.CreateGroupResponseDto;
import park.brothers.runwith_back.domain.Group.dto.Response.GetGroupResponseDto;
import park.brothers.runwith_back.domain.Group.dto.Response.ReviseGroupResponseDto;
import park.brothers.runwith_back.domain.Group.service.GroupService;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
class GroupControllerTest {

    private MockMvc mockMvc;

    @Mock
    private GroupService groupService;

    @InjectMocks
    private GroupController groupController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(groupController) //스프링 컨텍스트 없이 테스트 수행
                .alwaysDo(print()) // 모든 요청에 대해 로그 출력
                .build();
    }


    @Test
    @DisplayName("그룹 객체 저장 성공 테스트")
    void save() throws Exception {
        //given
        String groupId = UUID.randomUUID().toString();
        CreateGroupRequestDto createGroupRequestDto = new CreateGroupRequestDto(
            "test_name",
                groupId,
                "test_nickname",
                0,
                "test_description"
        );
        CreateGroupResponseDto createGroupResponseDto = new CreateGroupResponseDto(
                groupId,
                "test_name",
                "test_description",
                "test_imageLink",
                0
        );

        //가정 설정
        given(groupService.save(any(CreateGroupRequestDto.class), any(MultipartFile.class))).willReturn(createGroupResponseDto);

        //when & then
        String content = new ObjectMapper().writeValueAsString(createGroupRequestDto);
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
        mockMvc.perform(multipart("/api/v1/groups") // POST 요청 URL
                        .file(requestPart) //JSON데이터 추가
                        .file(imagePart)   //image데이터 추가
                        .contentType(MediaType.MULTIPART_FORM_DATA) // 전체 Content-Type
                        .accept(MediaType.APPLICATION_JSON)) // JSON 응답을 기대함
                .andExpect(status().isCreated()) //
                .andExpect(jsonPath("$.groupId").value(groupId)) // groupId 확인
                .andExpect(jsonPath("$.groupName").value("test_name")) // 이름 확인
                .andExpect(jsonPath("$.groupDescription").value("test_description"))
                .andExpect(jsonPath("$.groupImageLink").value("test_imageLink"));

    }

    @Test
    @DisplayName("해당 단어가 들어간 이름을 가진 모든 그룹 찾기 성공 컨트롤러 테스트")
    void findGroupsBySimilarName() throws Exception {
        //given
        List<GetGroupResponseDto> resultGroups = getGetGroupResponseDtos();
        given(groupService.getGroupsBySimilarName(anyString())).willReturn(resultGroups);

        //when & then
        mockMvc.perform(get("/api/v1/groups/test")) // get요청
                .andExpect(status().isOk()) //
                .andExpect(jsonPath("$", hasSize(2))) //전체 길이가 2인지 확인
                .andExpect(jsonPath("$[0].groupId").value(resultGroups.getFirst().getGroupId())) // runnerId 확인
                .andExpect(jsonPath("$[0].groupName").value("test_name1")) // 이름 확인
                .andExpect(jsonPath("$[0].groupDescription").value("test_description1"))
                .andExpect(jsonPath("$[0].groupImageLink").value("test_imageLink1"))
                .andExpect(jsonPath("$[1].groupId").value(resultGroups.getLast().getGroupId())) // runnerId 확인
                .andExpect(jsonPath("$[1].groupName").value("test_name2")) // 이름 확인
                .andExpect(jsonPath("$[1].groupDescription").value("test_description2"))
                .andExpect(jsonPath("$[1].groupImageLink").value("test_imageLink2"));


    }

    @Test
    @DisplayName("모든 그룹 조회 성공 테스트")
    void getAllGroups() throws Exception {
        //given
        List<GetGroupResponseDto> resultGroups = getGetGroupResponseDtos();
        given(groupService.getAllGroups()).willReturn(resultGroups);

        //when & then
        mockMvc.perform(get("/api/v1/groups")) // get요청
                .andExpect(status().isOk()) //
                .andExpect(jsonPath("$", hasSize(2))) //전체 길이가 2인지 확인
                .andExpect(jsonPath("$[0].groupId").value(resultGroups.getFirst().getGroupId())) // runnerId 확인
                .andExpect(jsonPath("$[0].groupName").value("test_name1")) // 이름 확인
                .andExpect(jsonPath("$[0].groupDescription").value("test_description1"))
                .andExpect(jsonPath("$[0].groupImageLink").value("test_imageLink1"))
                .andExpect(jsonPath("$[1].groupId").value(resultGroups.getLast().getGroupId())) // runnerId 확인
                .andExpect(jsonPath("$[1].groupName").value("test_name2")) // 이름 확인
                .andExpect(jsonPath("$[1].groupDescription").value("test_description2"))
                .andExpect(jsonPath("$[1].groupImageLink").value("test_imageLink2"));


    }

    @Test
    @DisplayName("그룹 삭제 성공 테스트")
    void deleteGroup() throws Exception {
        //given
        DeleteGroupRequestDto deleteGroupRequestDto = new DeleteGroupRequestDto(
          UUID.randomUUID().toString(),
          UUID.randomUUID().toString()
        );

        //when & then
        //JSON형식의 문자열로 반환
        String content = new ObjectMapper().writeValueAsString(deleteGroupRequestDto);

        mockMvc.perform(delete("/api/v1/groups") // DELETE 요청 URL
                        .contentType(MediaType.APPLICATION_JSON) // 요청 타입 확인
                        .content(content)) // Body에 JSON 문자열 담기
                .andExpect(status().isOk()) //
                .andExpect(jsonPath("$.message").value("그룹이 성공적으로 삭제되었습니다."));
    }

    @Test
    @DisplayName("그룹 정보 수정 성공 컨트롤러 테스트")
    void reviseGroupInfo() throws Exception {
        String groupId = UUID.randomUUID().toString();
        String runnerId = UUID.randomUUID().toString();
        ReviseGroupRequestDto reviseGroupRequestDto = new ReviseGroupRequestDto(
                runnerId,
                0,
                "test_description",
                "test_imageLink"
        );
        ReviseGroupResponseDto reviseGroupResponseDto = new ReviseGroupResponseDto(
                groupId,
                "test_revised_name",
                "test_revised_description",
                "test_revised_imageLink",
                0
        );

        given(groupService.reviseGroup(anyString(), any(ReviseGroupRequestDto.class), any(MultipartFile.class))).willReturn(reviseGroupResponseDto);

        //when & then
        //JSON형식의 문자열로 반환

        String content = new ObjectMapper().writeValueAsString(reviseGroupRequestDto);
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

        mockMvc.perform(multipart("/api/v1/groups/{groupId}", groupId)
                        .file(requestPart) //JSON데이터 추가
                        .file(imagePart)   //image데이터 추가
                        .contentType(MediaType.MULTIPART_FORM_DATA) // 전체 Content-Type
                        .accept(MediaType.APPLICATION_JSON)) // JSON 응답을 기대함
                .andExpect(status().isOk()) //
                .andExpect(jsonPath("$.groupId").value(groupId))
                .andExpect(jsonPath("$.groupName").value("test_revised_name"))
                .andExpect(jsonPath("$.groupDescription").value("test_revised_description"))
                .andExpect(jsonPath("$.groupImageLink").value("test_revised_imageLink"));
    }

    // 중복 코드 메서드화(GetGroupResponseDto로 이루어진 리스트 만들기)
    private static @NonNull List<GetGroupResponseDto> getGetGroupResponseDtos() {
        GetGroupResponseDto getGroupResponseDto1 = new GetGroupResponseDto(
                UUID.randomUUID().toString(),
                "test_name1",
                "test_description1",
                "test_imageLink1"
        );
        GetGroupResponseDto getGroupResponseDto2 = new GetGroupResponseDto(
                UUID.randomUUID().toString(),
                "test_name2",
                "test_description2",
                "test_imageLink2"
        );
        return List.of(
                getGroupResponseDto1,
                getGroupResponseDto2
        );
    }
}