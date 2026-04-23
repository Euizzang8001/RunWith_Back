package park.brothers.runwith_back.domain.Runner.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import park.brothers.runwith_back.domain.Runner.entity.Runner;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JPARunnerRepository.class)
class JPARunnerRepositoryTest {

    @Autowired
    private JPARunnerRepository runnerRepository;

    @Test
    @DisplayName("러너 저장하기 성공 테스트")
    void save() {
        //given
        //러너 객체 생성
        Runner runner = new Runner();
        runner.setId("test_runner");
        runner.setName("test_name");
        runner.setCreatedAt(LocalDateTime.now());

        //when
        Runner savedRunner = runnerRepository.save(runner);

        //then
        // 저장된 러너는 id와 createdAt이 있어야 함.
        assertThat(savedRunner.getId()).isEqualTo(runner.getId());
        assertThat(savedRunner.getCreatedAt()).isNotNull();
        // 입력한 Runner와 같은 값을 가져야 함.
        assertThat(savedRunner.getName()).isEqualTo(runner.getName());
    }

    @Test
    @DisplayName("id로 러너 찾기 레퍼지토리 성공 테스트")
    void findById() {
        //given
        //러너 객체 저장
        Runner runner = new Runner();
        runner.setId("test_runner");
        runner.setName("test_name");
        Runner savedRunner = runnerRepository.save(runner);

        //when
        //이메일로 러너 찾기
        Optional<Runner> foundRunner = runnerRepository.findById(savedRunner.getId());

        //then
        //id로 찾았을 때, 러너는 반드시 존재해야 하고, id가 저장된 러너와 동일해야 한다.
        assertThat(foundRunner.isPresent()).isTrue();
        assertThat(foundRunner.get().getId()).isEqualTo(savedRunner.getId());
    }

    @Test
    @DisplayName("Name으로 러너 찾기 레퍼지토리 성공 테스트")
    void findByName() {
        //given
        //러너 객체 저장
        Runner runner = new Runner();
        runner.setId("test_runner");
        runner.setName("test_name");
        runnerRepository.save(runner);

        //when
        //이름으로 러너 찾기
        Optional<Runner> foundRunner = runnerRepository.findByName("test_name");

        //then
        //이름으로 찾았을 때, 러너는 반드시 존재해야 하고, 이름이 저장하려는 러너와 동일해야 한다.
        assertThat(foundRunner.isPresent()).isTrue();
        assertThat(foundRunner.get().getName()).isEqualTo(runner.getName());
    }
}