package park.brothers.runwith_back.domain.JoinRequest.service;

import jakarta.validation.Valid;
import park.brothers.runwith_back.domain.JoinRequest.dto.request.CreateJoinRequestRequestDto;
import park.brothers.runwith_back.domain.JoinRequest.dto.response.CreateJoinRequestResponseDto;
import park.brothers.runwith_back.domain.JoinRequest.dto.response.GetJoinRequestResponseDto;
import park.brothers.runwith_back.domain.JoinRequest.dto.response.GetMyJoinRequestsResponseDto;

import java.util.List;

public interface JoinRequestService {

    CreateJoinRequestResponseDto save(String runnerId, @Valid CreateJoinRequestRequestDto createJoinRequestRequestDto);

    void delete(String runnerId, @Valid String joinRequestId);

    void accept(String runnerId, @Valid String joinRequestId);

    void reject(String runnerId, @Valid String joinRequestId);

    List<GetJoinRequestResponseDto> getJoinRequestOfGroup(String runnerId, @Valid String groupId);

    List<GetMyJoinRequestsResponseDto> getMyJoinRequest(String runnerId);
}
