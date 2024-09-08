package com.shashank.electronic.store.dtos;


import com.shashank.electronic.store.models.RefreshToken;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JwtResponse {

    private String token;

    UserDTO user;

//    private String jwtToken;

    private RefreshTokenDTO refreshTokenDTO;

}
