package park.brothers.runwith_back.domain.Recognize.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import park.brothers.runwith_back.common.Exceptions.NotAcceptableException;
import park.brothers.runwith_back.common.Exceptions.ResourceNotFoundException;
import park.brothers.runwith_back.common.Exceptions.UnauthorizedException;
import park.brothers.runwith_back.domain.Belong.entity.Belong;
import park.brothers.runwith_back.domain.Belong.repository.BelongRepository;
import park.brothers.runwith_back.domain.Recognize.dto.Request.ChangeRecognizeRequestDto;
import park.brothers.runwith_back.domain.Recognize.dto.Response.ChangeRecognizeResponseDto;
import park.brothers.runwith_back.domain.Recognize.entity.Recognize;
import park.brothers.runwith_back.domain.Recognize.repository.RecognizeRepository;
import park.brothers.runwith_back.domain.Runner.entity.Runner;
import park.brothers.runwith_back.domain.Runner.repository.RunnerRepository;
import park.brothers.runwith_back.domain.Schedule.entity.Schedule;
import park.brothers.runwith_back.domain.Schedule.repository.ScheduleRepository;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class Version1RecognizeService implements RecognizeService {

    private final RunnerRepository runnerRepository;
    private final ScheduleRepository scheduleRepository;
    private final BelongRepository belongRepository;
    private final RecognizeRepository recognizeRepository;

    @Override
    public ChangeRecognizeResponseDto changeRecognize(String runnerId, ChangeRecognizeRequestDto changeRecognizeRequestDto, String scheduleId) {
        // runner가 존재해야 함
        Optional<Runner> runner = runnerRepository.findById(runnerId);
        if(runner.isEmpty()){
            throw new ResourceNotFoundException("존재하지 않는 러너입니다.");
        }

        //schedule이 존재해야 함
        Optional<Schedule> schedule = scheduleRepository.findScheduleById(UUID.fromString(scheduleId));
        if(schedule.isEmpty()){
            throw new ResourceNotFoundException("존재하지 않는 스케줄입니다.");
        }

        //스케줄의 주인이 러너여서는 안됨
        if(schedule.get().getBelong().getRunner().getId().equals(runnerId)){
            throw new NotAcceptableException("나의 스케줄에는 인정할 수 없습니다.");
        }

        //인정하려는 러너가 스케줄과 같은 그룹이어야 함
        UUID groupId = schedule.get().getBelong().getGroup().getId();
        Optional<Belong> belong = belongRepository.findByRunnerIdAndGroupId(runnerId, groupId);
        if(belong.isEmpty()){
            throw new UnauthorizedException("같은 그룹원만 인정할 수 있습니다.");
        }

        //인정하기 객체가 없으면 객체 생성, 있으면 바꾸기만 하기
        Optional<Recognize> recognize = recognizeRepository.findByBelongIdAndScheduleId(belong.get().getId(), schedule.get().getId());
        if(recognize.isEmpty()) {
            Recognize newRecognize = new Recognize();
            newRecognize.setRecognizingBelong(belong.get());
            newRecognize.setRecognizedSchedule(schedule.get());
            newRecognize.setRecognizing(changeRecognizeRequestDto.isRecognizing());

            Recognize savedRecognize = recognizeRepository.save(newRecognize);

            return new ChangeRecognizeResponseDto(
                    savedRecognize.getRecognizedSchedule().getId().toString(),
                    savedRecognize.isRecognizing()
            );
        } else {
            Recognize revisedRecognize = recognizeRepository.revise(recognize.get(), changeRecognizeRequestDto.isRecognizing());
            return new ChangeRecognizeResponseDto(
                    revisedRecognize.getRecognizedSchedule().getId().toString(),
                    revisedRecognize.isRecognizing()
            );
        }
    }
}
