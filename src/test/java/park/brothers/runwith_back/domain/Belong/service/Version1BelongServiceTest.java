package park.brothers.runwith_back.domain.Belong.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import park.brothers.runwith_back.common.Exceptions.DuplicateResourceException;
import park.brothers.runwith_back.common.Exceptions.NotAcceptableException;
import park.brothers.runwith_back.common.Exceptions.ResourceNotFoundException;
import park.brothers.runwith_back.common.Exceptions.UnauthorizedException;
import park.brothers.runwith_back.domain.Belong.dto.Request.ChangeLeaderRequestDto;
import park.brothers.runwith_back.domain.Belong.dto.Request.CreateBelongRequestDto;
import park.brothers.runwith_back.domain.Belong.dto.Request.DeleteBelongRequestDto;
import park.brothers.runwith_back.domain.Belong.dto.Response.CreateBelongResponseDto;
import park.brothers.runwith_back.domain.Belong.entity.Belong;
import park.brothers.runwith_back.domain.Belong.repository.BelongRepository;
import park.brothers.runwith_back.domain.Group.dto.Response.GetGroupResponseDto;
import park.brothers.runwith_back.domain.Group.entity.Group;
import park.brothers.runwith_back.domain.Group.repository.GroupRepository;
import park.brothers.runwith_back.domain.Runner.dto.Response.GetRunnerResponseDto;
import park.brothers.runwith_back.domain.Runner.entity.Runner;
import park.brothers.runwith_back.domain.Runner.repository.RunnerRepository;
import park.brothers.runwith_back.external.AWS_S3.AWSS3Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class Version1BelongServiceTest {

    @Mock
    private BelongRepository belongRepository;

    @Mock
    private RunnerRepository runnerRepository;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private AWSS3Service awss3Service;

    @InjectMocks
    private Version1BelongService belongService;

    @Test
    @DisplayName("그룹 참여 실패 서비스 테스트 - 이미 가입된 그룹")
    void joinGroupFailByAlreadyJoin() {
        //given
        UUID runnerUUID = UUID.randomUUID();
        UUID groupUUID = UUID.randomUUID();

        CreateBelongRequestDto createBelongRequestDto = new CreateBelongRequestDto(
                runnerUUID.toString(),
                groupUUID.toString(),
                "test_nickname"
        );

        given(belongRepository.findByRunnerIdAndGroupId(any(), any())).willReturn(Optional.of(new Belong()));

        // when & then
        assertThrows(DuplicateResourceException.class, () -> belongService.joinGroup(createBelongRequestDto));
        verify(belongRepository, times(1)).findByRunnerIdAndGroupId(any(), any());
        verify(belongRepository, times(0)).findByGroupIdAndNickname(any(), anyString());
        verify(runnerRepository, times(0)).findById(any());
        verify(groupRepository, times(0)).findById(any());
        verify(belongRepository, times(0)).save(any(Belong.class));
    }

    @Test
    @DisplayName("그룹 참여 실패 서비스 테스트 - 그룹 내 이미 존재하는 닉네임")
    void joinGroupFailByAlreadyUsedNickname() {
        //given
        UUID runnerUUID = UUID.randomUUID();
        UUID groupUUID = UUID.randomUUID();

        CreateBelongRequestDto createBelongRequestDto = new CreateBelongRequestDto(
                runnerUUID.toString(),
                groupUUID.toString(),
                "test_nickname"
        );

        given(belongRepository.findByRunnerIdAndGroupId(any(), any())).willReturn(Optional.empty());
        given(belongRepository.findByGroupIdAndNickname(any(), anyString())).willReturn(Optional.of(new Belong()));


        // when & then
        assertThrows(DuplicateResourceException.class, () -> belongService.joinGroup(createBelongRequestDto));
        verify(belongRepository, times(1)).findByRunnerIdAndGroupId(any(), any());
        verify(belongRepository, times(1)).findByGroupIdAndNickname(any(), anyString());
        verify(runnerRepository, times(0)).findById(any());
        verify(groupRepository, times(0)).findById(any());
        verify(belongRepository, times(0)).save(any(Belong.class));
    }

    @Test
    @DisplayName("그룹 참여 실패 서비스 테스트 - 존재하지 않는 러너")
    void joinGroupFailByNotFoundRunner() {
        //given
        UUID runnerUUID = UUID.randomUUID();
        UUID groupUUID = UUID.randomUUID();

        CreateBelongRequestDto createBelongRequestDto = new CreateBelongRequestDto(
                runnerUUID.toString(),
                groupUUID.toString(),
                "test_nickname"
        );

        given(belongRepository.findByRunnerIdAndGroupId(any(), any())).willReturn(Optional.empty());
        given(belongRepository.findByGroupIdAndNickname(any(), anyString())).willReturn(Optional.empty());
        given(runnerRepository.findById(any())).willReturn(Optional.empty());


        // when & then
        assertThrows(ResourceNotFoundException.class, () -> belongService.joinGroup(createBelongRequestDto));
        verify(belongRepository, times(1)).findByRunnerIdAndGroupId(any(), any());
        verify(belongRepository, times(1)).findByGroupIdAndNickname(any(), anyString());
        verify(runnerRepository, times(1)).findById(any());
        verify(groupRepository, times(0)).findById(any());
        verify(belongRepository, times(0)).save(any(Belong.class));
    }

    @Test
    @DisplayName("그룹 참여 실패 서비스 테스트 - 존재하지 않는 그룹")
    void joinGroupFailByNotFoundGroup() {
        //given
        UUID runnerUUID = UUID.randomUUID();
        UUID groupUUID = UUID.randomUUID();
        UUID belongUUID = UUID.randomUUID();

        CreateBelongRequestDto createBelongRequestDto = new CreateBelongRequestDto(
                runnerUUID.toString(),
                groupUUID.toString(),
                "test_nickname"
        );

        given(belongRepository.findByRunnerIdAndGroupId(any(), any())).willReturn(Optional.empty());
        given(belongRepository.findByGroupIdAndNickname(any(), anyString())).willReturn(Optional.empty());

        Runner runner = new Runner();
        runner.setId(UUID.randomUUID());
        runner.setName("test_runner");
        runner.setEmail("test_email");
        runner.setPassword("test_password");
        runner.setCreatedAt(LocalDateTime.now());
        given(runnerRepository.findById(any())).willReturn(Optional.of(runner));

        given(groupRepository.findById(any())).willReturn(Optional.empty());


        // when & then
        assertThrows(ResourceNotFoundException.class, () -> belongService.joinGroup(createBelongRequestDto));
        verify(belongRepository, times(1)).findByRunnerIdAndGroupId(any(), any());
        verify(belongRepository, times(1)).findByGroupIdAndNickname(any(), anyString());
        verify(runnerRepository, times(1)).findById(any());
        verify(groupRepository, times(1)).findById(any());
        verify(belongRepository, times(0)).save(any(Belong.class));
    }

    @Test
    @DisplayName("그룹 참여 성공 서비스 테스트")
    void joinGroupSuccess() {
        //given
        UUID runnerUUID = UUID.randomUUID();
        UUID groupUUID = UUID.randomUUID();

        CreateBelongRequestDto createBelongRequestDto = new CreateBelongRequestDto(
                runnerUUID.toString(),
                groupUUID.toString(),
                "test_nickname"
        );

        given(belongRepository.findByRunnerIdAndGroupId(any(), any())).willReturn(Optional.empty());
        given(belongRepository.findByGroupIdAndNickname(any(), anyString())).willReturn(Optional.empty());

        Runner runner = new Runner();
        runner.setId(runnerUUID);
        runner.setName("test_runner");
        runner.setEmail("test_email");
        runner.setPassword("test_password");
        runner.setCreatedAt(LocalDateTime.now());
        given(runnerRepository.findById(any())).willReturn(Optional.of(runner));

        Group group = new Group();
        group.setId(groupUUID);
        group.setDescription("test_description");
        group.setName("test_group");
        group.setCertificationCriteria(0);
        group.setIsSelf(false);
        group.setCreatedAt(LocalDateTime.now());

        given(groupRepository.findById(any())).willReturn(Optional.of(group));

        Belong belong = new Belong();
        UUID belongUUID = UUID.randomUUID();
        belong.setId(belongUUID);
        belong.setNickname("test_nickname");
        belong.setGroup(group);
        belong.setRunner(runner);
        belong.setLeader(false);
        belong.setJoinAt(LocalDateTime.now());
        given(belongRepository.save(any(Belong.class))).willReturn(belong);

        CreateBelongResponseDto createBelongResponseDto = new CreateBelongResponseDto(
                belongUUID.toString(),
                runnerUUID.toString(),
                groupUUID.toString(),
                "test_nickname",
                false
        );


        // when & then
        assertThat(belongService.joinGroup(createBelongRequestDto)).isEqualTo(createBelongResponseDto);
        verify(belongRepository, times(1)).findByRunnerIdAndGroupId(any(), any());
        verify(belongRepository, times(1)).findByGroupIdAndNickname(any(), anyString());
        verify(runnerRepository, times(1)).findById(any());
        verify(groupRepository, times(1)).findById(any());
        verify(belongRepository, times(1)).save(any(Belong.class));
    }

    @Test
    @DisplayName("그룹 탈퇴 실패 서비스 테스트 - 존재하지 않는 belong")
    void leaveGroupFailByNotExistBelong() {
        //given
        UUID groupUUID = UUID.randomUUID();
        UUID runnerUUID = UUID.randomUUID();
        UUID belongUUID = UUID.randomUUID();

        DeleteBelongRequestDto deleteBelongRequestDto = new DeleteBelongRequestDto(
                runnerUUID.toString(),
                groupUUID.toString()
        );

        given(belongRepository.findByRunnerIdAndGroupId(any(), any())).willReturn(Optional.empty());

        //when &then
        assertThrows(ResourceNotFoundException.class, () -> belongService.leaveGroup(belongUUID.toString(), deleteBelongRequestDto));
        verify(belongRepository, times(1)).findByRunnerIdAndGroupId(any(), any());
        verify(belongRepository, times(0)).deleteByRunnerIdAndGroupId(any(), any());
    }

    @Test
    @DisplayName("그룹 탈퇴 실패 서비스 테스트 - belong정보가 러너, 그룹과 일치하지 않음")
    void leaveGroupFailByNotCorrect() {
        //given
        UUID groupUUID = UUID.randomUUID();
        UUID runnerUUID = UUID.randomUUID();
        UUID belongUUID = UUID.randomUUID();

        DeleteBelongRequestDto deleteBelongRequestDto = new DeleteBelongRequestDto(
                runnerUUID.toString(),
                groupUUID.toString()
        );
        Runner runner = new Runner();
        runner.setId(runnerUUID);

        Group group = new Group();
        group.setId(groupUUID);


        Belong belong = new Belong();
        belong.setId(UUID.randomUUID());
        belong.setRunner(runner);
        belong.setGroup(group);
        given(belongRepository.findByRunnerIdAndGroupId(any(), any())).willReturn(Optional.of(belong));

        //when &then
        assertThrows(NotAcceptableException.class, () -> belongService.leaveGroup(belongUUID.toString(), deleteBelongRequestDto));
        verify(belongRepository, times(1)).findByRunnerIdAndGroupId(any(), any());
        verify(belongRepository, times(0)).deleteByRunnerIdAndGroupId(any(), any());
    }

    @Test
    @DisplayName("그룹 탈퇴 성공 서비스 테스트")
    void leaveGroupSuccess() {
        //given
        UUID groupUUID = UUID.randomUUID();
        UUID runnerUUID = UUID.randomUUID();
        UUID belongUUID = UUID.randomUUID();

        DeleteBelongRequestDto deleteBelongRequestDto = new DeleteBelongRequestDto(
                runnerUUID.toString(),
                groupUUID.toString()
        );
        Runner runner = new Runner();
        runner.setId(runnerUUID);

        Group group = new Group();
        group.setId(groupUUID);


        Belong belong = new Belong();
        belong.setId(belongUUID);
        belong.setRunner(runner);
        belong.setGroup(group);
        belong.setLeader(false);
        belong.setNickname("test_nickname");
        belong.setJoinAt(LocalDateTime.now());

        given(belongRepository.findByRunnerIdAndGroupId(any(), any())).willReturn(Optional.of(belong));

        belongService.leaveGroup(belongUUID.toString(), deleteBelongRequestDto);

        //when &then
        verify(belongRepository, times(1)).findByRunnerIdAndGroupId(any(), any());
        verify(belongRepository, times(1)).deleteByRunnerIdAndGroupId(any(), any());
    }

    @Test
    @DisplayName("특정 러너가 속한 모든 그룹 찾기 성공 서비스 테스트")
    void getAllGroupsRunnerJoin() {
        //gien
        Runner runner = new Runner();
        UUID runnerUUID = UUID.randomUUID();
        runner.setId(runnerUUID);

        Belong belong1 = new Belong();
        Group group1 = new Group();
        UUID group1UUID = UUID.randomUUID();
        UUID belong1UUID = UUID.randomUUID();
        belong1.setId(belong1UUID);
        group1.setId(group1UUID);
        group1.setName("test_group1");
        group1.setDescription("test_description1");
        belong1.setGroup(group1);
        belong1.setRunner(runner);

        Belong belong2 = new Belong();
        Group group2 = new Group();
        UUID group2UUID = UUID.randomUUID();
        UUID belong2UUID = UUID.randomUUID();
        belong2.setId(belong2UUID);
        group2.setId(group2UUID);
        group2.setName("test_group2");
        group2.setDescription("test_description2");
        belong2.setGroup(group2);
        belong2.setRunner(runner);

        given(belongRepository.findByRunnerId(any())).willReturn(List.of(belong1, belong2));
        given(awss3Service.getImagePresignedUrl(anyString(), anyString(), anyInt())).willReturn(null);

        //when & then
        assertThat(belongService.getAllGroupsRunnerJoin(runnerUUID.toString())).isEqualTo(List.of(
                new GetGroupResponseDto(
                        group1UUID.toString(),
                        "test_group1",
                        "test_description1",
                        null
                ),
                new GetGroupResponseDto(
                        group2UUID.toString(),
                        "test_group2",
                        "test_description2",
                        null
                )
        ));
    }

    @Test
    @DisplayName("특정 그룹에 속한 러너 가져오기 성공 서비스 테스트")
    void getAllRunnersInGroup() {
        //given
        Group group = new Group();
        UUID groupUUID = UUID.randomUUID();
        group.setId(groupUUID);

        Belong belong1 = new Belong();
        Runner runner1 = new Runner();
        UUID runner1UUID = UUID.randomUUID();
        UUID belong1UUID = UUID.randomUUID();
        belong1.setId(belong1UUID);
        runner1.setId(runner1UUID);
        runner1.setName("test_runner1");
        belong1.setRunner(runner1);
        belong1.setGroup(group);

        Belong belong2 = new Belong();
        Runner runner2 = new Runner();
        UUID runner2UUID = UUID.randomUUID();
        UUID belong2UUID = UUID.randomUUID();
        belong2.setId(belong2UUID);
        runner2.setId(runner2UUID);
        runner2.setName("test_runner2");
        belong2.setRunner(runner2);
        belong2.setGroup(group);

        given(belongRepository.findByGroupId(any())).willReturn(List.of(belong1, belong2));
        given(awss3Service.getImagePresignedUrl(anyString(), anyString(), anyInt())).willReturn(null);

        //when & then
        assertThat(belongService.getAllRunnersInGroup(groupUUID.toString())).isEqualTo(List.of(
                new GetRunnerResponseDto(
                        runner1UUID.toString(),
                        "test_runner1",
                        null
                ),
                new GetRunnerResponseDto(
                        runner2UUID.toString(),
                        "test_runner2",
                        null
                )
        ));
    }

    @Test
    @DisplayName("리더 변경 실패 서비스 테스트 - 이전 리더로 요청한 리더가 그룹에 속하지 않음")
    void changeLeaderFailByNotInGroup() {
        UUID oldLeaderRunnerUUID = UUID.randomUUID();
        Runner oldRunner = new Runner();
        oldRunner.setId(oldLeaderRunnerUUID);

        UUID newLeaderRunnerUUID = UUID.randomUUID();
        UUID groupUUID = UUID.randomUUID();

        ChangeLeaderRequestDto changeLeaderRequestDto = new ChangeLeaderRequestDto(
                newLeaderRunnerUUID.toString(),
                groupUUID.toString()
        );

        given(belongRepository.findByRunnerIdAndGroupId(any(), any())).willReturn(Optional.empty());

        assertThrows(UnauthorizedException.class, () ->
                belongService.changeLeader(oldRunner.getId().toString(), changeLeaderRequestDto));
        verify(belongRepository, times(1)).findByRunnerIdAndGroupId(any(), any());
        verify(belongRepository, times(0)).changeIsLeader(any(), any(), anyBoolean());
    }

    @Test
    @DisplayName("리더 변경 실패 서비스 테스트 - 리더가 아님")
    void changeLeaderFailByNotLeader() {
        UUID oldLeaderRunnerUUID = UUID.randomUUID();
        Runner oldRunner = new Runner();
        oldRunner.setId(oldLeaderRunnerUUID);

        UUID newLeaderRunnerUUID = UUID.randomUUID();
        UUID groupUUID = UUID.randomUUID();

        ChangeLeaderRequestDto changeLeaderRequestDto = new ChangeLeaderRequestDto(
                newLeaderRunnerUUID.toString(),
                groupUUID.toString()
        );

        Belong belong = new Belong();
        belong.setRunner(oldRunner);
        belong.setLeader(false);

        given(belongRepository.findByRunnerIdAndGroupId(any(), any())).willReturn(Optional.of(belong));

        assertThrows(UnauthorizedException.class, () ->
                belongService.changeLeader(oldRunner.getId().toString(), changeLeaderRequestDto));
        verify(belongRepository, times(1)).findByRunnerIdAndGroupId(any(), any());
        verify(belongRepository, times(0)).changeIsLeader(any(), any(), anyBoolean());
    }

    @Test
    @DisplayName("리더 변경 성공 서비스 테스트")
    void changeLeaderSuccess() {
        UUID oldLeaderRunnerUUID = UUID.randomUUID();
        Runner oldRunner = new Runner();
        oldRunner.setId(oldLeaderRunnerUUID);

        UUID newLeaderRunnerUUID = UUID.randomUUID();
        UUID groupUUID = UUID.randomUUID();

        ChangeLeaderRequestDto changeLeaderRequestDto = new ChangeLeaderRequestDto(
                newLeaderRunnerUUID.toString(),
                groupUUID.toString()
        );

        Belong belong = new Belong();
        belong.setRunner(oldRunner);
        belong.setLeader(true);

        given(belongRepository.findByRunnerIdAndGroupId(any(), any())).willReturn(Optional.of(belong));

        belongService.changeLeader(oldRunner.getId().toString(), changeLeaderRequestDto);
        verify(belongRepository, times(1)).findByRunnerIdAndGroupId(any(), any());
        verify(belongRepository, times(2)).changeIsLeader(any(), any(), anyBoolean());
    }
}