package park.brothers.runwith_back.domain.Recognize.service;

import jakarta.validation.Valid;
import park.brothers.runwith_back.domain.Recognize.dto.Request.ChangeRecognizeRequestDto;
import park.brothers.runwith_back.domain.Recognize.dto.Response.ChangeRecognizeResponseDto;

public interface RecognizeService {
    ChangeRecognizeResponseDto changeRecognize(String runnerId, @Valid ChangeRecognizeRequestDto changeRecognizeRequestDto, String scheduleId);
}
