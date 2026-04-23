package park.brothers.runwith_back.domain.Runner.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import park.brothers.runwith_back.domain.Belong.entity.Belong;
import park.brothers.runwith_back.domain.Belong.repository.BelongRepository;
import park.brothers.runwith_back.domain.Group.entity.Group;
import park.brothers.runwith_back.domain.Group.repository.GroupRepository;
import park.brothers.runwith_back.domain.Runner.dto.Request.CreateRunnerRequestDto;
import park.brothers.runwith_back.domain.Runner.dto.Response.CreateRunnerResponseDto;
import park.brothers.runwith_back.domain.Runner.entity.Runner;
import park.brothers.runwith_back.domain.Runner.repository.RunnerRepository;
import park.brothers.runwith_back.external.AWS_S3.AWSS3Service;

import java.io.IOException;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class Version1RunnerServiceTest {
    @InjectMocks
    private Version1RunnerService runnerService;

    @Mock
    private RunnerRepository runnerRepository;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private BelongRepository belongRepository;

    @Mock
    private AWSS3Service awss3Service;

    @Test
    @DisplayName("러너 저장 서비스 성공 테스트")
    void save() throws IOException {
        //given
        //service 입력값
        CreateRunnerRequestDto createRunnerRequestDto = new CreateRunnerRequestDto(
          "test_name"
        );

        //가짜 러너 생성
        Runner mockRunner = new Runner();
        String mockRunnerId = "test_runner";
        mockRunner.setId(mockRunnerId);
        mockRunner.setName("test_name");

        //가짜 이미지 선언
        MockMultipartFile dummyImage = new MockMultipartFile(
                "image", "test.jpg", "image/jpeg", "dummy_data".getBytes()
        );

        //러너 저장 시, 러너 올바르게 반환
        given(runnerRepository.save(any(Runner.class))).willReturn(mockRunner);
        // 그룹 저장 시, 그룹 객체 반환
        given(groupRepository.save(any(Group.class))).willReturn(new Group());
        // 빌롱 저장 시, 빌롱 객체 반환
        given(belongRepository.save(any(Belong.class))).willReturn(new Belong());
        // 이미지 저장 시, 가짜 링크 return
        given(awss3Service.putImageToAWSS3(any(MultipartFile.class), anyString(), any(), anyInt())).willReturn("test_imageLink");

        //when
        CreateRunnerResponseDto fakeResponse = runnerService.save(mockRunnerId, createRunnerRequestDto, dummyImage);

        //then
        assertThat(fakeResponse).isNotNull(); //응답값은 null이면 안됨
        //응답값은 아래 값들을 가져야만 한다.
        assertThat(fakeResponse.getRunnerName()).isEqualTo("test_name");
        //repository.save() 코드들은 1번식만 실행되어야 한다.
        verify(runnerRepository, times(1)).save(any(Runner.class));
        verify(groupRepository, times(1)).save(any(Group.class));
        verify(belongRepository, times(1)).save(any(Belong.class));
    }
}