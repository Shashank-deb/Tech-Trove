package com.shashank.electronic.store;

import com.shashank.electronic.store.models.User;
import com.shashank.electronic.store.repositories.UserRepository;
import com.shashank.electronic.store.security.JwtHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
class ElectronicStoreApplicationTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtHelper jwtHelper;

    @Test
    void contextLoads() {
    }

    //	Testing jwt token

    @Test
    void testToken() {
        User user = userRepository.findByEmail("shashank@gmail.com").get();
        String token = jwtHelper.generateToken(user);
        System.out.println(token);

        //getting username from token

        String username = jwtHelper.getUsernameFromToken(token);
        System.out.println(username);

        //checking wheather token is expired or not

        boolean isTokenExpired = jwtHelper.isTokenExpired(token);
        System.out.println("Token Expired: "+isTokenExpired);






    }
}
