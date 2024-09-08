package com.shashank.electronic.store.services.impl;

import com.shashank.electronic.store.dtos.RefreshTokenDTO;
import com.shashank.electronic.store.dtos.UserDTO;
import com.shashank.electronic.store.exceptions.ResourceNotFoundException;
import com.shashank.electronic.store.models.RefreshToken;
import com.shashank.electronic.store.models.User;
import com.shashank.electronic.store.repositories.RefreshTokenRepository;
import com.shashank.electronic.store.repositories.UserRepository;
import com.shashank.electronic.store.services.RefreshTokenService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;


@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {


    private UserRepository userRepository;


    private RefreshTokenRepository refreshTokenRepository;


    private ModelMapper mapper;

    public RefreshTokenServiceImpl(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository, ModelMapper mapper) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.mapper = mapper;
    }

    @Override
    public RefreshTokenDTO createRefreshToken(String username) {
        User user = userRepository.findByEmail(username).orElseThrow(() -> new ResourceNotFoundException("user not found exception !"));


        RefreshToken refreshToken = refreshTokenRepository.findByUser(user).orElse(null);

        if (refreshToken == null) {
            refreshToken = RefreshToken.builder()
                    .user(user)
                    .token(UUID.randomUUID().toString())
                    .expiryDate(Instant.now().plusSeconds(5 * 24 * 60 * 60))
                    .build();

        } else {
            refreshToken.setToken(UUID.randomUUID().toString());
            refreshToken.setExpiryDate(Instant.now().plusSeconds(5 * 24 * 60 * 60));
        }


        RefreshToken savedToken = refreshTokenRepository.save(refreshToken);
        return this.mapper.map(savedToken, RefreshTokenDTO.class);

    }

    @Override
    public RefreshTokenDTO findByToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token).orElseThrow(() -> new ResourceNotFoundException("Refresh Token not found exception !"));
        return mapper.map(refreshToken, RefreshTokenDTO.class);
    }

    @Override
    public RefreshTokenDTO verifyRefreshToken(RefreshTokenDTO token) {
        var refreshToken = mapper.map(token, RefreshToken.class);
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(refreshToken);
            throw new RuntimeException("Refresh Token Expired");
        }

        return token;
    }

    @Override
    public UserDTO getUser(RefreshTokenDTO dto) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(dto.getToken()).orElseThrow(() -> new ResourceNotFoundException("Refresh Token not found exception !"));
        User user = refreshToken.getUser();
        return mapper.map(user, UserDTO.class);
    }


}
