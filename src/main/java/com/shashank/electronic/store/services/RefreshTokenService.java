package com.shashank.electronic.store.services;

import com.shashank.electronic.store.dtos.RefreshTokenDTO;
import com.shashank.electronic.store.dtos.UserDTO;
import com.shashank.electronic.store.models.RefreshToken;

public interface RefreshTokenService {

    //create

    RefreshTokenDTO createRefreshToken(String username);

    //find by token
    RefreshTokenDTO findByToken(String token);

    //verify the token

    RefreshTokenDTO verifyRefreshToken(RefreshTokenDTO token);


    UserDTO getUser(RefreshTokenDTO dto);
}
