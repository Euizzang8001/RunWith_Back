package park.brothers.runwith_back.domain.Group.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import park.brothers.runwith_back.domain.Belong.entity.Belong;
import park.brothers.runwith_back.domain.Belong.repository.BelongRepository;
import park.brothers.runwith_back.domain.Group.dto.Request.CreateGroupRequestDto;
import park.brothers.runwith_back.domain.Group.dto.Response.CreateGroupResponseDto;
import park.brothers.runwith_back.domain.Group.dto.Response.GetGroupResponseDto;
import park.brothers.runwith_back.domain.Group.entity.Group;
import park.brothers.runwith_back.domain.Group.repository.GroupRepository;
import park.brothers.runwith_back.domain.Runner.entity.Runner;
import park.brothers.runwith_back.domain.Runner.repository.RunnerRepository;

import java.util.List;
import java.util.Optional;

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

    @InjectMocks
    private Version1GroupService groupService;

    @Test
    @DisplayName("그룹 저장 성공 서비스 테스트")
    void saveSuccess() {
        //given
        CreateGroupRequestDto createGroupRequestDto = new CreateGroupRequestDto(
                "test_name",
                0L,
                "test_nickname",
                0,
                "test_description",
                "test_imageLink"
        );

        //레퍼지토리에서 반환할 러너
        Runner runner = new Runner();
        runner.setId(0L);
        runner.setName("test_runner_name");
        runner.setPassword("test_runner_password");
        runner.setEmail("test_runner_email");
        runner.setImageLink("test_runner_imageLink");

        //레퍼지토리에서 반환할 그룹
        Group group = new Group();
        group.setId(0L);
        group.setName("test_group_name");
        group.setDescription("test_group_description");
        group.setImageLink("test_group_imageLink");
        group.setCertificationCriteria(0);

        //레퍼지토리 부분 가정
        given(runnerRepository.findById(anyLong())).willReturn(Optional.of(runner));
        given(groupRepository.findByName(anyString())).willReturn(null);
        given(groupRepository.save(any(Group.class))).willReturn(group);

        //when
        CreateGroupResponseDto fakeResponse = groupService.save(createGroupRequestDto);
        //then
        assertThat(fakeResponse).isNotNull(); //응답값은 null이면 안됨
        //응답값은 아래 값들을 가져야만 한다.
        assertThat(fakeResponse.getName()).isEqualTo("test_group_name");
        assertThat(fakeResponse.getDescription()).isEqualTo("test_group_description");
        assertThat(fakeResponse.getImageLink()).isEqualTo("test_group_imageLink");
        assertThat(fakeResponse.getCertificationCriteria()).isEqualTo(0);
        //repository.save() 코드들은 1번식만 실행되어야 한다.
        verify(runnerRepository, times(1)).findById(anyLong());
        verify(groupRepository, times(1)).findByName(anyString());
        verify(groupRepository, times(1)).save(any(Group.class));
        verify(belongRepository, times(1)).save(any(Belong.class));

    }

    @Test
    @DisplayName("그룹 생성 실패 서비스 테스트 - 존재하는 그룹 이름")
    void saveFailByDuplicationGroupName(){
        //given
        CreateGroupRequestDto createGroupRequestDto = new CreateGroupRequestDto(
                "test_name",
                0L,
                "test_nickname",
                0,
                "test_description",
                "test_imageLink"
        );

        //레퍼지토리에서 반환할 그룹
        Group group = new Group();
        group.setId(0L);
        group.setName("test_group_name");
        group.setDescription("test_group_description");
        group.setImageLink("test_group_imageLink");
        group.setCertificationCriteria(0);

        given(groupRepository.findByName(anyString())).willReturn(group);

        //when & then
        assertThrows(IllegalAccessError.class, () -> {
            groupService.save(createGroupRequestDto);
        });

        //repository.save() 코드들은 1번식만 실행되어야 한다.
        verify(groupRepository, times(1)).findByName(anyString());
        verify(runnerRepository, times(0)).findById(anyLong());
        verify(groupRepository, times(0)).save(any(Group.class));
        verify(belongRepository, times(0)).save(any(Belong.class));


    }

    @Test
    @DisplayName("그룹 생성 실패 서비스 테스트 - 러너가 존재하지 않음")
    void saveFailByNotExistRunner(){
        //given
        CreateGroupRequestDto createGroupRequestDto = new CreateGroupRequestDto(
                "test_name",
                0L,
                "test_nickname",
                0,
                "test_description",
                "test_imageLink"
        );

        given(groupRepository.findByName(anyString())).willReturn(null);
        //없는 러너라고 나타나야 한다.
        given(runnerRepository.findById(anyLong())).willReturn(Optional.empty());

        //when & then
        assertThrows(IllegalAccessError.class, () -> {
            groupService.save(createGroupRequestDto);
        });

        //repository.save() 코드들은 1번식만 실행되어야 한다.
        verify(groupRepository, times(1)).findByName(anyString());
        verify(runnerRepository, times(1)).findById(anyLong());
        verify(groupRepository, times(0)).save(any(Group.class));
        verify(belongRepository, times(0)).save(any(Belong.class));
    }

    @Test
    @DisplayName("전체 그룹 조회 성공 서비스 테스트")
    void getAllGroups() {
        //given
        Group group1 = createNewGroup(1L, "test_group_name1", "test_group_description1", 0);
        Group group2 = createNewGroup(2L, "test_group_name2", "test_group_description2", 1);

        List<Group> groupList = List.of(group1, group2);

        //when
        given(groupRepository.findAll()).willReturn(groupList);

        //then
        List<GetGroupResponseDto> fakeResponse = groupService.getAllGroups();
        assertThat(fakeResponse.size()).isEqualTo(2);
        assertThat(fakeResponse.get(0).getId()).isEqualTo(1L);
        assertThat(fakeResponse.get(1).getId()).isEqualTo(2L);

        verify(groupRepository, times(1)).findAll();

    }

    @Test
    @DisplayName("유사 이름 그룹 조회 성공 서비스 테스트")
    void getGroupsBySimilarName() {
        //given
        Group group1 = createNewGroup(1L, "test_group_name1", "test_group_description1", 0);
        Group group2 = createNewGroup(2L, "test_group_name2", "test_group_description2", 1);

        List<Group> groupList = List.of(group1, group2);

        //when
        given(groupRepository.findBySimilarName(anyString())).willReturn(groupList);

        //then
        List<GetGroupResponseDto> fakeResponse = groupService.getGroupsBySimilarName("test");
        assertThat(fakeResponse.size()).isEqualTo(2);
        assertThat(fakeResponse.get(0).getId()).isEqualTo(1L);
        assertThat(fakeResponse.get(1).getId()).isEqualTo(2L);

        verify(groupRepository, times(1)).findBySimilarName(anyString());
    }

    //그룹 생성 자동화 메서드
    private static Group createNewGroup(Long id, String name, String description, int certificationCriteria) {
        Group group = new Group();
        group.setId(id);
        group.setName(name);
        group.setDescription(description);
        group.setCertificationCriteria(certificationCriteria);
        group.setIsSelf(false);
        return group;
    }
}