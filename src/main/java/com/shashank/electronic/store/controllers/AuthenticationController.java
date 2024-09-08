package com.shashank.electronic.store.controllers;


import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.apache.v2.ApacheHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.shashank.electronic.store.dtos.*;
import com.shashank.electronic.store.exceptions.BadApiRequestException;
import com.shashank.electronic.store.exceptions.ResourceNotFoundException;
import com.shashank.electronic.store.models.Providers;
import com.shashank.electronic.store.models.User;
import com.shashank.electronic.store.security.JwtHelper;
import com.shashank.electronic.store.services.RefreshTokenService;
import com.shashank.electronic.store.services.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@RestController
@RequestMapping("/auth")
@Tag(name = "AuthenticationController", description = "REST APIs related to Authentication operations")

public class AuthenticationController {


    private Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private ModelMapper mapper;

    @Value("${app.google.client-id}")
    private String googleClientId;

    @Value("${app.google.default-password}")
    private String googleProviderDefaultPassword;

    @Autowired
    private UserService userService;


    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RefreshTokenService refreshTokenService;

    //method to generate the token:

    @PostMapping("/generate-token")
    public ResponseEntity<JwtResponse> login(
            @RequestBody JwtRequest request
    ) {


        logger.info("Username {} , Password {} ", request.getEmail(), request.getPassword());
        this.doAuthenticate(request.getEmail(), request.getPassword());

        UserDetails user = (User) userDetailsService.loadUserByUsername(request.getEmail());
        //generate token

        String token = jwtHelper.generateToken(user);
        //send karna hai response


        //Refresh token
        RefreshTokenDTO refreshToken = refreshTokenService.createRefreshToken(user.getUsername());


        JwtResponse jwtResponse = JwtResponse
                .builder()
                .token(token)
                .user(mapper.map(user, UserDTO.class))
                .refreshTokenDTO(refreshToken)
                .build();

        return ResponseEntity.ok(jwtResponse);
    }

    private void doAuthenticate(String email, String password) {
        try {
            Authentication authentication = new UsernamePasswordAuthenticationToken(email, password);
            authenticationManager.authenticate(authentication);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Bad Credentials");
        }

    }


    //handle Login with Google
    @PostMapping("/login-with-google")
    public ResponseEntity<JwtResponse> loginWithGoogle(@RequestBody GoogleLoginRequest request) throws GeneralSecurityException, IOException {
        logger.info("Id Token: {}", request.getIdToken());
        //verification of the token is done by google
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new ApacheHttpTransport(), new GsonFactory()).setAudience(List.of(googleClientId)).build();
        GoogleIdToken googleIdToken = verifier.verify(request.getIdToken());
        if (googleIdToken != null) {
            //token is verified
            GoogleIdToken.Payload payload = googleIdToken.getPayload();

            // Print user identifier
            String userId = payload.getSubject();
            System.out.println("User ID: " + userId);

            // Get profile information from payload
            String email = payload.getEmail();
            boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
            String name = (String) payload.get("name");
            String pictureUrl = (String) payload.get("picture");
            String locale = (String) payload.get("locale");
            String familyName = (String) payload.get("family_name");
            String givenName = (String) payload.get("given_name");


            logger.info("Email: {}, Email Verified: {}, Name: {}, Picture Url: {}, Locale: {}, Family Name: {}, Given Name: {}", email, emailVerified, name, pictureUrl, locale, familyName, givenName);


            UserDTO userDTO = new UserDTO();
            userDTO.setName(name);
            userDTO.setEmail(email);
            userDTO.setImageName(pictureUrl);
            userDTO.setPassword(googleProviderDefaultPassword);
            userDTO.setAbout("User is created using Google ");
            userDTO.setProviders(Providers.GOOGLE);


            UserDTO user = null;

            try {
                logger.info("user is loaded from database  !");
                user = userService.getUserByEmail(userDTO.getEmail());

                //logic implement about whether we can load user from database or my using the mail only one
                //provider is google or not is also checked

                logger.info("User Provider : " + user.getProviders().toString());
                if (user.getProviders().equals(userDTO.getProviders())) {
                    //continue
                } else {
                    throw new BadCredentialsException("Your email is already register !! try to login with username and password ");
                }
            } catch (ResourceNotFoundException ex) {
                logger.info("This time user created , because this is new user !");
                user = userService.createUser(userDTO);
            }


            //generate token
            this.doAuthenticate(user.getEmail(), userDTO.getPassword());


            User user1 = mapper.map(user, User.class);
            String token = jwtHelper.generateToken(user1);
            //send karna hai response

            JwtResponse jwtResponse = JwtResponse.builder().token(token).user(user).build();

            return ResponseEntity.ok(jwtResponse);


        } else {
            //token is not verified
            logger.info("Google token not verified or invalid !");
            throw new BadApiRequestException("Google token not verified or invalid! ");
        }


    }


    @PostMapping("/regenerate-token")
    public ResponseEntity<JwtResponse> regenerateToken(@RequestBody RefreshTokenRequestDTO request) {

        RefreshTokenDTO refreshTokenDTO = refreshTokenService.findByToken(request.getRefreshToken());
        RefreshTokenDTO refreshTokenDTO1 = refreshTokenService.verifyRefreshToken(refreshTokenDTO);

        UserDTO user = refreshTokenService.getUser(refreshTokenDTO1);

        String jwtToken = jwtHelper.generateToken(mapper.map(user, User.class));

        //apki choice kya he new banana chahte ho to new bana lo or pura to pura bana lo
        JwtResponse response = JwtResponse.builder()
                .token(jwtToken)
                .refreshTokenDTO(refreshTokenDTO)
                .user(user)
                .build();

        return ResponseEntity.ok(response);


    }
}
