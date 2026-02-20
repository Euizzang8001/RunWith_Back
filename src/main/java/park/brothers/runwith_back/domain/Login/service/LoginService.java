package park.brothers.runwith_back.domain.Login.service;

import park.brothers.runwith_back.domain.Login.dto.Request.LoginRequestDto;

public interface LoginService {
    String login(LoginRequestDto loginRequestDto);
}
