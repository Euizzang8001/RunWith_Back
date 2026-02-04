package park.brothers.runwith_back.domain.Login.service;

import park.brothers.runwith_back.domain.Login.dto.LoginRequestDto;

public interface LoginService {
    Long login(LoginRequestDto loginRequestDto);
}
