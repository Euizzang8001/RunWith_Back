package park.brothers.runwith_back.domain.Group.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import park.brothers.runwith_back.domain.Belong.entity.Belong;
import park.brothers.runwith_back.domain.Belong.repository.BelongRepository;
import park.brothers.runwith_back.domain.Group.dto.Request.CreateGroupRequestDto;
import park.brothers.runwith_back.domain.Group.dto.Request.ReviseGroupRequestDto;
import park.brothers.runwith_back.domain.Group.dto.Response.CreateGroupResponseDto;
import park.brothers.runwith_back.domain.Group.dto.Response.GetGroupResponseDto;
import park.brothers.runwith_back.domain.Group.dto.Response.ReviseGroupResponseDto;
import park.brothers.runwith_back.domain.Group.entity.Group;
import park.brothers.runwith_back.domain.Group.repository.GroupRepository;
import park.brothers.runwith_back.domain.Runner.entity.Runner;
import park.brothers.runwith_back.domain.Runner.repository.RunnerRepository;
import park.brothers.runwith_back.external.AWS_S3.AWSS3Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class Version1GroupServiceTest {

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private RunnerRepository runnerRepository;

    @Mock
    private BelongRepository belongRepository;

    @Mock
    private AWSS3Service awss3Service;

    @InjectMocks
    private Version1GroupService groupService;

    @Test
    @DisplayName("그룹 저장 성공 서비스 테스트")
    void saveSuccess() throws IOException {
        //given
        CreateGroupRequestDto createGroupRequestDto = new CreateGroupRequestDto(
                "test_name",
                "test_nickname",
                0,
                "test_description"
        );

        //레퍼지토리에서 반환할 러너
        Runner runner = new Runner();
        runner.setId("test_runner");
        runner.setName("test_runner_name");

        //레퍼지토리에서 반환할 그룹
        Group group = new Group();
        UUID groupId = UUID.randomUUID();
        group.setId(groupId);
        group.setName("test_name");
        group.setDescription("test_description");
        group.setCertificationCriteria(0);

        //가짜 이미지 선언
        MockMultipartFile dummyImage = new MockMultipartFile(
                "image", "test.jpg", "image/jpg", "dummy_data".getBytes()
        );

        //레퍼지토리 부분 가정
        given(runnerRepository.findById(anyString())).willReturn(Optional.of(runner));
        given(groupRepository.findByName(anyString())).willReturn(Optional.empty());
        given(groupRepository.save(any(Group.class))).willReturn(group);

        // 이미지 저장 시, 가짜 링크 return
        given(awss3Service.putImageToAWSS3(any(), anyString(), any(), anyInt())).willReturn("test_imageLink");

        //when
        CreateGroupResponseDto fakeResponse = groupService.save("test_runner", createGroupRequestDto, dummyImage);
        //then
        assertThat(fakeResponse).isNotNull(); //응답값은 null이면 안됨
        //응답값은 아래 값들을 가져야만 한다.
        assertThat(fakeResponse.getGroupName()).isEqualTo("test_name");
        assertThat(fakeResponse.getGroupDescription()).isEqualTo("test_description");
        assertThat(fakeResponse.getGroupImageLink()).isEqualTo("test_imageLink");
        assertThat(fakeResponse.getGroupCertificationCriteria()).isEqualTo(0);
        //repository.save() 코드들은 1번식만 실행되어야 한다.
        verify(runnerRepository, times(1)).findById(anyString());
        verify(groupRepository, times(1)).findByName(anyString());
        verify(groupRepository, times(1)).save(any(Group.class));
        verify(belongRepository, times(1)).save(any(Belong.class));

    }

    @Test
    @DisplayName("그룹 생성 실패 서비스 테스트 - 존재하는 그룹 이름")
    void saveFailByDuplicationGroupName() {
        //given
        CreateGroupRequestDto createGroupRequestDto = new CreateGroupRequestDto(
                "test_name",
                "test_nickname",
                0,
                "test_description"
        );

        //레퍼지토리에서 반환할 그룹
        Group group = new Group();
        group.setId(UUID.randomUUID());
        group.setName("test_group_name");
        group.setDescription("test_group_description");
        group.setCertificationCriteria(0);

        given(groupRepository.findByName(anyString())).willReturn(Optional.of(group));

        //가짜 이미지 선언
        MockMultipartFile dummyImage = new MockMultipartFile(
                "image", "test.jpg", "image/jpeg", "dummy_data".getBytes()
        );
        //when & then
        assertThrows(IllegalAccessError.class, () -> groupService.save(anyString(), createGroupRequestDto, dummyImage));

        //repository.save() 코드들은 1번식만 실행되어야 한다.
        verify(groupRepository, times(1)).findByName(anyString());
        verify(runnerRepository, times(0)).findById(anyString());
        verify(groupRepository, times(0)).save(any(Group.class));
        verify(belongRepository, times(0)).save(any(Belong.class));


    }

    @Test
    @DisplayName("그룹 생성 실패 서비스 테스트 - 러너가 존재하지 않음")
    void saveFailByNotExistRunner() {
        //given
        CreateGroupRequestDto createGroupRequestDto = new CreateGroupRequestDto(
                "test_name",
                "test_nickname",
                0,
                "test_description"
        );
        given(groupRepository.findByName(anyString())).willReturn(Optional.empty());

        //가짜 이미지 선언
        MockMultipartFile dummyImage = new MockMultipartFile(
                "image", "test.jpg", "image/jpeg", "dummy_data".getBytes()
        );
        //없는 러너라고 나타나야 한다.
        given(runnerRepository.findById(anyString())).willReturn(Optional.empty());


        //when & then
        assertThrows(IllegalAccessError.class, () -> groupService.save(anyString(), createGroupRequestDto, dummyImage));

        //repository.save() 코드들은 1번식만 실행되어야 한다.
        verify(groupRepository, times(1)).findByName(anyString());
        verify(runnerRepository, times(1)).findById(anyString());
        verify(groupRepository, times(0)).save(any(Group.class));
        verify(belongRepository, times(0)).save(any(Belong.class));
    }

    @Test
    @DisplayName("전체 그룹 조회 성공 서비스 테스트")
    void getAllGroups() {
        //given
        String group1Id = UUID.randomUUID().toString();
        String group2Id = UUID.randomUUID().toString();
        Group group1 = createNewGroup(group1Id, "test_group_name1", "test_group_description1", 0);
        Group group2 = createNewGroup(group2Id, "test_group_name2", "test_group_description2", 1);

        List<Group> groupList = List.of(group1, group2);

        //when
        given(groupRepository.findAll()).willReturn(groupList);

        //then
        List<GetGroupResponseDto> fakeResponse = groupService.getAllGroups();
        assertThat(fakeResponse.size()).isEqualTo(2);
        assertThat(fakeResponse.get(0).getGroupId()).isEqualTo(group1Id);
        assertThat(fakeResponse.get(1).getGroupId()).isEqualTo(group2Id);

        verify(groupRepository, times(1)).findAll();

    }

    @Test
    @DisplayName("유사 이름 그룹 조회 성공 서비스 테스트")
    void getGroupsBySimilarName() {
        //given
        String group1Id = UUID.randomUUID().toString();
        String group2Id = UUID.randomUUID().toString();
        Group group1 = createNewGroup(group1Id, "test_group_name1", "test_group_description1", 0);
        Group group2 = createNewGroup(group2Id, "test_group_name2", "test_group_description2", 1);

        List<Group> groupList = List.of(group1, group2);

        //when
        given(groupRepository.findBySimilarName(anyString())).willReturn(groupList);

        //then
        List<GetGroupResponseDto> fakeResponse = groupService.getGroupsBySimilarName("test");
        assertThat(fakeResponse.size()).isEqualTo(2);
        assertThat(fakeResponse.get(0).getGroupId()).isEqualTo(group1Id);
        assertThat(fakeResponse.get(1).getGroupId()).isEqualTo(group2Id);

        verify(groupRepository, times(1)).findBySimilarName(anyString());
    }

    //그룹 생성 자동화 메서드
    private static Group createNewGroup(String stringId, String name, String description, int certificationCriteria) {
        Group group = new Group();
        group.setId(UUID.fromString(stringId));
        group.setName(name);
        group.setDescription(description);
        group.setCertificationCriteria(certificationCriteria);
        group.setIsSelf(false);
        return group;
    }

    @Test
    @DisplayName("그룹 삭제 성공 서비스 테스트")
    void deleteSuccess(){
        //given
        //request 설정
        String groupId = UUID.randomUUID().toString();
        String runnerId = "test_runner";
        Group group = new Group();
        group.setId(UUID.fromString(groupId));
        given(groupRepository.findById(any(UUID.class))).willReturn(Optional.of(group));

        Runner runner = new Runner();
        runner.setId(runnerId);
        given(runnerRepository.findById(anyString())).willReturn(Optional.of(runner));

        Belong belong = new Belong();
        belong.setRunner(runner);
        belong.setGroup(group);
        belong.setLeader(true);

        given(belongRepository.findByGroupId(any(UUID.class))).willReturn(List.of(belong));

        //when
        groupService.delete(runnerId, groupId);

        //then
        verify(groupRepository, times(1)).findById(any(UUID.class));
        verify(runnerRepository, times(1)).findById(anyString());
        verify(belongRepository, times(1)).findByGroupId(any(UUID.class));
        verify(belongRepository, times(1)).deleteByRunnerIdAndGroupId(anyString(), any(UUID.class));
        verify(groupRepository, times(1)).delete(any(Group.class));
    }

    @Test
    @DisplayName("그룹 삭제 실패 서비스 테스트 - 존재하지 않는 그룹")
    void deleteFailByNotExistGroup(){
        //given
        String groupId = UUID.randomUUID().toString();
        String runnerId = "test_runner";

        given(groupRepository.findById(any(UUID.class))).willReturn(Optional.empty());

        //when & then
        assertThrows(IllegalAccessError.class, () -> groupService.delete(runnerId, groupId));

        verify(groupRepository, times(1)).findById(any(UUID.class));
        verify(runnerRepository, times(0)).findById(anyString());
        verify(belongRepository, times(0)).findByGroupId(any(UUID.class));
        verify(belongRepository, times(0)).deleteByRunnerIdAndGroupId(anyString(), any(UUID.class));
        verify(groupRepository, times(0)).delete(any(Group.class));
    }

    @Test
    @DisplayName("그룹 삭제 실패 서비스 테스트 - 존재하지 않는 러너")
    void deleteFailByNotExistRunner(){
        //given
        String groupId = UUID.randomUUID().toString();
        String runnerId = "test_runner";
        Group group = new Group();
        group.setId(UUID.fromString(groupId));
        given(groupRepository.findById(any(UUID.class))).willReturn(Optional.of(group));

        given(runnerRepository.findById(anyString())).willReturn(Optional.empty());

        //when & then
        assertThrows(IllegalAccessError.class, () -> groupService.delete(runnerId, groupId));

        //then
        verify(groupRepository, times(1)).findById(any(UUID.class));
        verify(runnerRepository, times(1)).findById(anyString());
        verify(belongRepository, times(0)).findByGroupId(any(UUID.class));
        verify(belongRepository, times(0)).deleteByRunnerIdAndGroupId(anyString(), any(UUID.class));
        verify(groupRepository, times(0)).delete(any(Group.class));
    }

    @Test
    @DisplayName("그룹 삭제 실패 서비스 테스트 - 그룹에 존재하는 인원이 2명 이상")
    void deleteFailByExistRunnersInGroup(){
        //given
        String groupId = UUID.randomUUID().toString();
        String runner1Id = "test_runner1";
        String runner2Id = "test_runner2";
        Group group = new Group();
        group.setId(UUID.fromString(groupId));
        given(groupRepository.findById(any(UUID.class))).willReturn(Optional.of(group));

        Runner runner1 = new Runner();
        runner1.setId(runner1Id);
        given(runnerRepository.findById(anyString())).willReturn(Optional.of(runner1));

        Runner runner2 = new Runner();
        runner2.setId(runner2Id);

        Belong belong1 = new Belong();
        belong1.setRunner(runner1);
        belong1.setGroup(group);
        belong1.setLeader(true);

        Belong belong2 = new Belong();
        belong2.setRunner(runner2);
        belong2.setGroup(group);
        belong2.setLeader(false);

        given(belongRepository.findByGroupId(any(UUID.class))).willReturn(List.of(belong1, belong2));

        //when & then
        assertThrows(IllegalAccessError.class, () -> groupService.delete(runner1Id, groupId));

        //then
        verify(groupRepository, times(1)).findById(any(UUID.class));
        verify(runnerRepository, times(1)).findById(anyString());
        verify(belongRepository, times(1)).findByGroupId(any(UUID.class));
        verify(belongRepository, times(0)).deleteByRunnerIdAndGroupId(anyString(), any(UUID.class));
        verify(groupRepository, times(0)).delete(any(Group.class));
    }

    @Test
    @DisplayName("그룹 삭제 실패 서비스 테스트 - 리더가 아님")
    void deleteFailByNotLeaderRunner(){
        //given
        //request 설정
        String groupId = UUID.randomUUID().toString();
        String runnerId = "test_runner";

        Group group = new Group();
        group.setId(UUID.fromString(groupId));
        given(groupRepository.findById(any(UUID.class))).willReturn(Optional.of(group));

        Runner runner = new Runner();
        runner.setId(runnerId);
        given(runnerRepository.findById(anyString())).willReturn(Optional.of(runner));

        Belong belong = new Belong();
        belong.setRunner(runner);
        belong.setGroup(group);
        belong.setLeader(false);

        given(belongRepository.findByGroupId(any(UUID.class))).willReturn(List.of(belong));

        //when
        assertThrows(IllegalAccessError.class, () -> groupService.delete(runnerId, groupId));

        //then
        verify(groupRepository, times(1)).findById(any(UUID.class));
        verify(runnerRepository, times(1)).findById(anyString());
        verify(belongRepository, times(1)).findByGroupId(any(UUID.class));
        verify(belongRepository, times(0)).deleteByRunnerIdAndGroupId(anyString(), any(UUID.class));
        verify(groupRepository, times(0)).delete(any(Group.class));
    }

    @Test
    @DisplayName("그룹 수정 성공 서비스 테스트")
    void ReviseSuccess() throws IOException {
        //given
        String groupId = UUID.randomUUID().toString();
        String runnerId = "test_runner";
        ReviseGroupRequestDto reviseGroupRequestDto = new ReviseGroupRequestDto(
                1,
                "test_revised_description"
        );

        Group group = new Group();
        group.setId(UUID.fromString(groupId));
        group.setName("test_group");
        group.setDescription("test_revised_description");
        group.setCertificationCriteria(0);
        given(groupRepository.findById(any(UUID.class))).willReturn(Optional.of(group));

        //가짜 이미지 선언
        MockMultipartFile dummyImage = new MockMultipartFile(
                "image", "test.jpg", "image/jpeg", "dummy_data".getBytes()
        );
        given(awss3Service.putImageToAWSS3(any(), anyString(), any(), anyInt())).willReturn("test_revised_imageLink");

        Runner runner = new Runner();
        runner.setId(runnerId);
        given(runnerRepository.findById(anyString())).willReturn(Optional.of(runner));

        Belong belong = new Belong();
        belong.setGroup(group);
        belong.setRunner(runner);
        belong.setLeader(true);
        given(belongRepository.findByRunnerIdAndGroupId(anyString(), any(UUID.class))).willReturn(Optional.of(belong));

        //when
        ReviseGroupResponseDto reviseGroupResponseDto = groupService.reviseGroup(runnerId, groupId, reviseGroupRequestDto, dummyImage);

        //then
        assertThat(reviseGroupResponseDto.getGroupCertificationCriteria()).isEqualTo(1);
        assertThat(reviseGroupResponseDto.getGroupDescription()).isEqualTo("test_revised_description");
        assertThat(reviseGroupResponseDto.getGroupImageLink()).isEqualTo("test_revised_imageLink");

    }

    @Test
    @DisplayName("그룹 수정 실패 서비스 테스트 - 존재하지 않는 그룹")
    void ReviseFailByNotExistGroup(){
        //given
        String groupId = UUID.randomUUID().toString();
        String runnerId = "test_runner";
        ReviseGroupRequestDto reviseGroupRequestDto = new ReviseGroupRequestDto(
                1,
                "test_revised_description"
        );
        given(groupRepository.findById(any(UUID.class))).willReturn(Optional.empty());

        //가짜 이미지 선언
        MockMultipartFile dummyImage = new MockMultipartFile(
                "image", "test.jpg", "image/jpeg", "dummy_data".getBytes()
        );

        //when & then
        assertThrows(IllegalAccessError.class, () -> groupService.reviseGroup(runnerId, groupId, reviseGroupRequestDto, dummyImage));

        verify(groupRepository, times(1)).findById(any(UUID.class));
        verify(runnerRepository, times(0)).findById(anyString());
        verify(belongRepository, times(0)).findByRunnerIdAndGroupId(anyString(), any(UUID.class));
    }

    @Test
    @DisplayName("그룹 수정 실패 서비스 테스트 - 존재하지 않는 러너")
    void ReviseFailByNotExistRunner() {
        //given
        String groupId = UUID.randomUUID().toString();
        String runnerId = "test_runner";
        ReviseGroupRequestDto reviseGroupRequestDto = new ReviseGroupRequestDto(
                1,
                "test_revised_description"
        );

        Group group = new Group();
        group.setId(UUID.fromString(groupId));
        group.setName("test_group");
        group.setDescription("test_description");
        group.setCertificationCriteria(0);
        given(groupRepository.findById(any(UUID.class))).willReturn(Optional.of(group));

        //가짜 이미지 선언
        MockMultipartFile dummyImage = new MockMultipartFile(
                "image", "test.jpg", "image/jpeg", "dummy_data".getBytes()
        );

        given(runnerRepository.findById(anyString())).willReturn(Optional.empty());

        //when & then
        assertThrows(IllegalAccessError.class, () -> groupService.reviseGroup(runnerId, groupId, reviseGroupRequestDto, dummyImage));

        verify(groupRepository, times(1)).findById(any(UUID.class));
        verify(runnerRepository, times(1)).findById(anyString());
        verify(belongRepository, times(0)).findByRunnerIdAndGroupId(anyString(), any(UUID.class));
    }

    @Test
    @DisplayName("그룹 수정 실패 서비스 테스트 - 그룹에 속하지 않는 오류")
    void ReviseFailByBelongToGroup() {
        //given
        String groupId = UUID.randomUUID().toString();
        String runnerId = "test_runner";
        ReviseGroupRequestDto reviseGroupRequestDto = new ReviseGroupRequestDto(
                1,
                "test_revised_description"
        );

        Group group = new Group();
        group.setId(UUID.fromString(groupId));
        group.setName("test_group");
        group.setDescription("test_description");
        group.setCertificationCriteria(0);
        given(groupRepository.findById(any(UUID.class))).willReturn(Optional.of(group));

        //가짜 이미지 선언
        MockMultipartFile dummyImage = new MockMultipartFile(
                "image", "test.jpg", "image/jpeg", "dummy_data".getBytes()
        );

        Runner runner = new Runner();
        runner.setId(runnerId);

        given(runnerRepository.findById(anyString())).willReturn(Optional.of(runner));


        given(belongRepository.findByRunnerIdAndGroupId(anyString(), any(UUID.class))).willReturn(Optional.empty());


        //when & then
        assertThrows(IllegalAccessError.class, () -> groupService.reviseGroup(runnerId, groupId, reviseGroupRequestDto, dummyImage));

        verify(groupRepository, times(1)).findById(any(UUID.class));
        verify(runnerRepository, times(1)).findById(anyString());
        verify(belongRepository, times(1)).findByRunnerIdAndGroupId(anyString(), any(UUID.class));
    }

    @Test
    @DisplayName("그룹 수정 실패 서비스 테스트 - 리더가 아닌 러너가 수정")
    void ReviseFailByNotLeaderRunner() {
        //given
        String groupId = UUID.randomUUID().toString();
        String runnerId = "test_runner";
        ReviseGroupRequestDto reviseGroupRequestDto = new ReviseGroupRequestDto(
                1,
                "test_revised_description"
        );

        Group group = new Group();
        group.setId(UUID.fromString(groupId));
        group.setName("test_group");
        group.setDescription("test_description");
        group.setCertificationCriteria(0);
        given(groupRepository.findById(any(UUID.class))).willReturn(Optional.of(group));

        //가짜 이미지 선언
        MockMultipartFile dummyImage = new MockMultipartFile(
                "image", "test.jpg", "image/jpeg", "dummy_data".getBytes()
        );


        Runner runner = new Runner();
        runner.setId(runnerId);
        given(runnerRepository.findById(anyString())).willReturn(Optional.of(runner));

        Belong belong = new Belong();
        belong.setGroup(group);
        belong.setRunner(runner);
        belong.setLeader(false);
        given(belongRepository.findByRunnerIdAndGroupId(anyString(), any(UUID.class))).willReturn(Optional.of(belong));

        //when & then
        assertThrows(IllegalAccessError.class, () -> groupService.reviseGroup(runnerId, groupId, reviseGroupRequestDto, dummyImage));

        verify(groupRepository, times(1)).findById(any(UUID.class));
        verify(runnerRepository, times(1)).findById(anyString());
        verify(belongRepository, times(1)).findByRunnerIdAndGroupId(anyString(), any(UUID.class));
    }
}