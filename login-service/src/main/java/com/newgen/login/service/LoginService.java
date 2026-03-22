package com.newgen.login.service;

import com.newgen.login.dto.LoginResponseDTO;
import com.newgen.login.dto.MpinLoginRequestDTO;
import com.newgen.login.dto.PasswordLoginRequestDTO;

public interface LoginService {

    LoginResponseDTO loginWithPassword(PasswordLoginRequestDTO request);

    LoginResponseDTO loginWithMpin(MpinLoginRequestDTO request);
}
