package park.brothers.runwith_back.domain.Runner.service;

import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;
import park.brothers.runwith_back.domain.Runner.dto.Request.CreateRunnerRequestDto;
import park.brothers.runwith_back.domain.Runner.dto.Response.CreateRunnerResponseDto;

import java.io.IOException;

public interface RunnerService {
    CreateRunnerResponseDto save(CreateRunnerRequestDto createRunnerRequestDto, MultipartFile image) throws IllegalAccessError, IOException;

    Boolean checkDuplication(@Valid CreateRunnerRequestDto createRunnerRequestDto);
}
