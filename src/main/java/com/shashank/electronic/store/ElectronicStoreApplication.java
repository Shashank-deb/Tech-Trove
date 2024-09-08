package com.shashank.electronic.store;

import com.shashank.electronic.store.config.AppConstants;
import com.shashank.electronic.store.models.Role;
import com.shashank.electronic.store.models.User;
import com.shashank.electronic.store.repositories.RoleRepository;
import com.shashank.electronic.store.repositories.UserRepository;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.List;
import java.util.UUID;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Electronic Store API", version = "v1", description = "Electronic Store API"))
public class ElectronicStoreApplication implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public static void main(String[] args) {
        SpringApplication.run(ElectronicStoreApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        Role roleAdmin = roleRepository.findByName("ROLE_" + AppConstants.ROLE_ADMIN).orElse(null);

        if (roleAdmin == null) {
            Role role1 = new Role();
            role1.setRoleId(UUID.randomUUID().toString());
            role1.setName("ROLE_" + AppConstants.ROLE_ADMIN);
            roleRepository.save(role1);
        }


        Role roleNormal = roleRepository.findByName("ROLE_" + AppConstants.ROLE_NORMAL).orElse(null);
        if (roleNormal == null) {
            Role role2 = new Role();
            role2.setRoleId(UUID.randomUUID().toString());
            role2.setName("ROLE_" + AppConstants.ROLE_NORMAL);
            roleRepository.save(role2);
        }


        //creating admin user
        User user = userRepository.findByEmail("shashank@gmail.com").orElse(null);

        if (user == null) {
            user = new User();
            user.setUserId(UUID.randomUUID().toString());
            user.setName("Shashank");
            user.setEmail("shashank@gmail.com");
            user.setPassword(passwordEncoder.encode("sha123"));
            user.setGender("Male");
            user.setAbout("I am Shashank Sharma");
            user.setImageName("default.png");
            user.setRoles(List.of(roleAdmin));
            userRepository.save(user);

        }
    }
}
