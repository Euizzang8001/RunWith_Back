package park.brothers.runwith_back.domain.Runner.service;

import com.google.firebase.auth.FirebaseAuthException;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;
import park.brothers.runwith_back.domain.Runner.dto.Request.CreateRunnerRequestDto;
import park.brothers.runwith_back.domain.Runner.dto.Request.ReviseMyInfoRequestDto;
import park.brothers.runwith_back.domain.Runner.dto.Response.CreateRunnerResponseDto;
import park.brothers.runwith_back.domain.Runner.dto.Response.GetMyInfoResponseDto;

import java.io.IOException;

public interface RunnerService {
    CreateRunnerResponseDto save(String runnerId, CreateRunnerRequestDto createRunnerRequestDto, MultipartFile image) throws IllegalAccessError, IOException;
    Boolean isSavedRunner(String runnerId);

    GetMyInfoResponseDto findRunner(@Valid String runnerId);

    GetMyInfoResponseDto revise(String runnerId, ReviseMyInfoRequestDto reviseMyInfoRequestDto, MultipartFile image) throws IOException;

    void deleteRunner(String runnerId) throws FirebaseAuthException;
}
