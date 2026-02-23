package park.brothers.runwith_back.domain.Belong.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import park.brothers.runwith_back.domain.Belong.entity.Belong;
import park.brothers.runwith_back.domain.Group.entity.Group;
import park.brothers.runwith_back.domain.Group.repository.JPAGroupRepository;
import park.brothers.runwith_back.domain.Runner.entity.Runner;
import park.brothers.runwith_back.domain.Runner.repository.JPARunnerRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({JPABelongRepository.class, JPARunnerRepository.class, JPAGroupRepository.class})
class JPABelongRepositoryTest {

    @Autowired
    private JPABelongRepository belongRepository;

    @Autowired
    private JPAGroupRepository groupRepository;

    @Autowired
    private JPARunnerRepository runnerRepository;

    @Test
    @DisplayName("러너id와 그룹id로 빌롱 찾기 성공 레퍼지토리 테스트")
    void findByRunnerIdAndGroupId() {
        Runner runner = new Runner();
        runner.setName("test_runner");
        runner.setEmail("test_email");
        runner.setPassword("test_password");
        runner.setImageLink("test_imageLink");
        Runner savedRunner = runnerRepository.save(runner); //러너여도 반드시 저장해주어야 한다.

        Group group = new Group();
        group.setName("test_group");
        group.setIsSelf(false);
        group.setDescription("test_group");
        group.setCertificationCriteria(0);
        Group savedGroup = groupRepository.save(group);

        Belong belong = new Belong();
        belong.setRunner(savedRunner);
        belong.setGroup(savedGroup);
        belong.setLeader(false);
        belong.setNickname("test_nickname");

        belongRepository.save(belong);

        //when
        Optional<Belong> foundBelong = belongRepository.findByRunnerIdAndGroupId(runner.getId(), group.getId());

        //then
        assertThat(foundBelong).isPresent();
        assertThat(foundBelong.get().getRunner()).isEqualTo(runner);
        assertThat(foundBelong.get().getGroup()).isEqualTo(group);
        assertThat(foundBelong.get().getNickname()).isEqualTo("test_nickname");
    }

    @Test
    @DisplayName("그룹id와 빌롱 닉네임으로 빌롱 찾기 성공 레퍼지토리 테스트")
    void findByGroupIdAndNickname() {
        //given
        Runner runner = new Runner();
        runner.setName("test_runner");
        runner.setEmail("test_email");
        runner.setPassword("test_password");
        runner.setImageLink("test_imageLink");
        Runner savedRunner = runnerRepository.save(runner); //러너여도 반드시 저장해주어야 한다.

        Group group = new Group();
        group.setName("test_group");
        group.setIsSelf(false);
        group.setDescription("test_group");
        group.setCertificationCriteria(0);
        Group savedGroup = groupRepository.save(group);

        Belong belong = new Belong();
        belong.setRunner(savedRunner);
        belong.setGroup(savedGroup);
        belong.setLeader(false);
        belong.setNickname("test_nickname");

        belongRepository.save(belong);

        //when
        Optional<Belong> foundBelong = belongRepository.findByGroupIdAndNickname(group.getId(), "test_nickname");

        //then
        assertThat(foundBelong).isPresent();
        assertThat(foundBelong.get().getRunner()).isEqualTo(runner);
        assertThat(foundBelong.get().getGroup()).isEqualTo(group);
        assertThat(foundBelong.get().getNickname()).isEqualTo("test_nickname");
    }

    @Test
    @DisplayName("빌롱 저장 성공 레퍼지토리 테스트")
    void save() {
        Belong belong = new Belong();

        Group group = new Group();
        UUID groupUUID = UUID.randomUUID();
        group.setId(groupUUID);

        Runner runner = new Runner();
        UUID runnerUUID = UUID.randomUUID();
        runner.setId(runnerUUID);

        belong.setGroup(group);
        belong.setRunner(runner);
        belong.setNickname("test_nickname");

        //when
        Belong savedBelong = belongRepository.save(belong);

        //then
        assertThat(savedBelong.getId()).isEqualTo(belong.getId());
        assertThat(savedBelong.getRunner()).isEqualTo(belong.getRunner());
        assertThat(savedBelong.getGroup()).isEqualTo(belong.getGroup());
        assertThat(savedBelong.getNickname()).isEqualTo(belong.getNickname());
        assertThat(savedBelong.getJoinAt()).isEqualTo(belong.getJoinAt());

    }

    @Test
    @DisplayName("러너 id와 그룹 id로 빌롱 삭제하기 성공 레퍼지토리 테스트")
    void deleteByRunnerIdAndGroupId() {
        Runner runner = new Runner();
        runner.setName("test_runner");
        runner.setEmail("test_email");
        runner.setPassword("test_password");
        runner.setImageLink("test_imageLink");
        Runner savedRunner = runnerRepository.save(runner); //러너여도 반드시 저장해주어야 한다.

        Group group = new Group();
        group.setName("test_group");
        group.setIsSelf(false);
        group.setDescription("test_group");
        group.setCertificationCriteria(0);
        Group savedGroup = groupRepository.save(group);

        Belong belong = new Belong();
        belong.setRunner(savedRunner);
        belong.setGroup(savedGroup);
        belong.setLeader(false);
        belong.setNickname("test_nickname");

        belongRepository.save(belong);

        //when
        belongRepository.deleteByRunnerIdAndGroupId(runner.getId(), group.getId());
        Optional<Belong> foundBelong = belongRepository.findByRunnerIdAndGroupId(runner.getId(), group.getId());

        //then
        assertThat(foundBelong).isEmpty();
    }

    @Test
    @DisplayName("러너id가 동일한 모든 빌롱 찾기")
    void findByRunnerId() {
        Runner runner1 = new Runner();
        runner1.setName("test_runner1");
        runner1.setPassword("test_runner1");
        runner1.setEmail("test_runner1_email");
        runner1.setImageLink("test_runner1_imageLink");
        Runner savedRunner1 = runnerRepository.save(runner1);

        Runner runner2 = new Runner();
        runner2.setName("test_runner2");
        runner2.setPassword("test_runner2");
        runner2.setEmail("test_runner2_email");
        runner2.setImageLink("test_runner2_imageLink");
        Runner savedRunner2 = runnerRepository.save(runner2);

        Group group1 = new Group();
        group1.setName("test_group1");
        group1.setDescription("test_group1");
        group1.setIsSelf(false);
        group1.setCertificationCriteria(0);
        Group savedGroup1 = groupRepository.save(group1);

        Group group2 = new Group();
        group2.setName("test_group2");
        group2.setDescription("test_group2");
        group2.setIsSelf(false);
        group2.setCertificationCriteria(0);
        Group savedGroup2 = groupRepository.save(group2);

        Belong belong1 = new Belong();
        belong1.setNickname("test_belong1");
        belong1.setRunner(savedRunner1);
        belong1.setGroup(savedGroup1);
        belong1.setLeader(false);
        Belong savedBelong1 = belongRepository.save(belong1);

        Belong belong2 = new Belong();
        belong2.setNickname("test_belong2");
        belong2.setRunner(savedRunner1);
        belong2.setGroup(savedGroup2);
        belong2.setLeader(false);
        Belong savedBelong2 = belongRepository.save(belong2);

        Belong belong3 = new Belong();
        belong3.setNickname("test_belong3");
        belong3.setRunner(savedRunner2);
        belong3.setGroup(savedGroup2);
        belong3.setLeader(false);
        belongRepository.save(belong3);

        //when
        List<Belong> result = belongRepository.findByRunnerId(savedRunner1.getId());

        //then
        assertThat(result.getFirst().getId()).isEqualTo(savedBelong1.getId());
        assertThat(result.getLast().getId()).isEqualTo(savedBelong2.getId());
    }

    @Test
    @DisplayName("그룹id가 동일한 모든 빌롱 찾기")
    void findByGroupId() {
        Runner runner1 = new Runner();
        runner1.setName("test_runner1");
        runner1.setPassword("test_runner1");
        runner1.setEmail("test_runner1_email");
        runner1.setImageLink("test_runner1_imageLink");
        Runner savedRunner1 = runnerRepository.save(runner1);

        Runner runner2 = new Runner();
        runner2.setName("test_runner2");
        runner2.setPassword("test_runner2");
        runner2.setEmail("test_runner2_email");
        runner2.setImageLink("test_runner2_imageLink");
        Runner savedRunner2 = runnerRepository.save(runner2);

        Group group1 = new Group();
        group1.setName("test_group1");
        group1.setDescription("test_group1");
        group1.setIsSelf(false);
        group1.setCertificationCriteria(0);
        Group savedGroup1 = groupRepository.save(group1);

        Group group2 = new Group();
        group2.setName("test_group2");
        group2.setDescription("test_group2");
        group2.setIsSelf(false);
        group2.setCertificationCriteria(0);
        Group savedGroup2 = groupRepository.save(group2);

        Belong belong1 = new Belong();
        belong1.setNickname("test_belong1");
        belong1.setRunner(savedRunner1);
        belong1.setGroup(savedGroup1);
        belong1.setLeader(false);
        belongRepository.save(belong1);

        Belong belong2 = new Belong();
        belong2.setNickname("test_belong2");
        belong2.setRunner(savedRunner1);
        belong2.setGroup(savedGroup2);
        belong2.setLeader(false);
        Belong savedBelong2 = belongRepository.save(belong2);

        Belong belong3 = new Belong();
        belong3.setNickname("test_belong3");
        belong3.setRunner(savedRunner2);
        belong3.setGroup(savedGroup2);
        belong3.setLeader(false);
        Belong savedBelong3 = belongRepository.save(belong3);

        //when
        List<Belong> result = belongRepository.findByGroupId(savedGroup2.getId());

        //then
        assertThat(result.getFirst().getId()).isEqualTo(savedBelong2.getId());
        assertThat(result.getLast().getId()).isEqualTo(savedBelong3.getId());
    }

    @Test
    @DisplayName("리더 변경하기 성공 레퍼지토리 테스트")
    void changeIsLeader() {
        //given
        Runner runner= new Runner();
        runner.setName("test_runner");
        runner.setPassword("test_runner");
        runner.setEmail("test_runner_email");
        runner.setImageLink("test_runner1_imageLink");
        Runner savedRunner = runnerRepository.save(runner);

        Group group = new Group();
        group.setName("test_group");
        group.setDescription("test_group");
        group.setIsSelf(false);
        group.setCertificationCriteria(0);
        Group savedGroup = groupRepository.save(group);

        Belong belong = new Belong();
        belong.setNickname("test_belong");
        belong.setRunner(savedRunner);
        belong.setGroup(savedGroup);
        belong.setLeader(false);
        Belong savedBelong = belongRepository.save(belong);

        //when
        belongRepository.changeIsLeader(savedRunner.getId(), savedGroup.getId(), true);
        Optional<Belong> foundBelong = belongRepository.findById(savedBelong.getId());
        //then
        assertThat(foundBelong).isPresent();
        assertThat(foundBelong.get().isLeader()).isEqualTo(true);
    }

    @Test
    @DisplayName("Id로 빌롱 찾기 성공 레퍼지토리 테스트")
    void findById() {
        //given
        Runner runner= new Runner();
        runner.setName("test_runner");
        runner.setPassword("test_runner");
        runner.setEmail("test_runner_email");
        runner.setImageLink("test_runner1_imageLink");
        Runner savedRunner = runnerRepository.save(runner);

        Group group = new Group();
        group.setName("test_group");
        group.setDescription("test_group");
        group.setIsSelf(false);
        group.setCertificationCriteria(0);
        Group savedGroup = groupRepository.save(group);

        Belong belong = new Belong();
        belong.setNickname("test_belong");
        belong.setRunner(savedRunner);
        belong.setGroup(savedGroup);
        belong.setLeader(false);
        Belong savedBelong = belongRepository.save(belong);

        //when
        Optional<Belong> foundBelong = belongRepository.findById(savedBelong.getId());

        //then
        assertThat(foundBelong).isPresent();
        assertThat(foundBelong.get().getId()).isEqualTo(savedBelong.getId());
    }
}