//package park.brothers.runwith_back.domain.Login.service;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import park.brothers.runwith_back.common.Exceptions.ResourceNotFoundException;
//import park.brothers.runwith_back.domain.Login.dto.Request.LoginRequestDto;
//import park.brothers.runwith_back.domain.Login.dto.Response.LoginResponseDto;
//import park.brothers.runwith_back.domain.Runner.entity.Runner;
//import park.brothers.runwith_back.domain.Runner.repository.RunnerRepository;
//import park.brothers.runwith_back.external.AWS_S3.AWSS3Service;
//
//import java.util.Optional;
//
//@Service
//@RequiredArgsConstructor
//public class Version1LoginService implements LoginService {
//
//    private final RunnerRepository runnerRepository;
//
//    private final AWSS3Service awss3Service;
//
//    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
//        Optional<Runner> runner = runnerRepository.findByEmail(loginRequestDto.getLoginEmail())
//                .filter(r -> r.getPassword().equals(loginRequestDto.getLoginPassword()));
//
//        //runner가 있으면 로그인 성공
//        if(runner.isEmpty()){
//            throw new ResourceNotFoundException("이메일 또는 비밀번호가 다릅니다.");
//        }
//        return new LoginResponseDto(
//                runner.get().getId().toString(),
//                runner.get().getName(),
//                awss3Service.getImagePresignedUrl("runners", runner.get().getId().toString(), 0)
//        );
//    }
//
//}
