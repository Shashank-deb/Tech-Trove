package com.shashank.electronic.store.services.impl;

import com.shashank.electronic.store.config.AppConstants;
import com.shashank.electronic.store.dtos.PageableResponse;
import com.shashank.electronic.store.dtos.UserDTO;
import com.shashank.electronic.store.exceptions.ResourceNotFoundException;
import com.shashank.electronic.store.helper.Helper;
import com.shashank.electronic.store.models.Role;
import com.shashank.electronic.store.models.User;
import com.shashank.electronic.store.repositories.RoleRepository;
import com.shashank.electronic.store.repositories.UserRepository;
import com.shashank.electronic.store.services.UserService;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper mapper;

    @Value("${user.profile.image.path}")
    private String imagePath;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Autowired
    private RoleRepository roleRepository;

    private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public UserDTO createUser(UserDTO userDTO) {
//        generate unique id in string format
        String userId = UUID.randomUUID().toString();
        userDTO.setUserId(userId);


//        dto to entity
        User user = dtoToEntity(userDTO);
//       password  encoding here
        user.setPassword(passwordEncoder.encode(user.getPassword()));

//        get the role
        Role role = new Role();
        role.setRoleId(UUID.randomUUID().toString());
        role.setName("ROLE_" + AppConstants.ROLE_NORMAL);

        Role roleNormal = roleRepository.findByName("ROLE_" + AppConstants.ROLE_NORMAL).orElse(role);

        user.setRoles(List.of(roleNormal));

        User savedUser = userRepository.save(user);
//        entity to dto
        return entityToDto(savedUser);
    }

    @Override
    public UserDTO updateUser(UserDTO userDTO, String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with given user id"));
        user.setName(userDTO.getName());
        user.setAbout(userDTO.getAbout());
        user.setGender(userDTO.getGender());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setImageName(userDTO.getImageName());

        //assign normal role to user
        //by default jo bhi api se user banega usko hum log normal user banayenge

        User updatedUser = userRepository.save(user);
        return entityToDto(updatedUser);
    }


    @Override
    public void deleteUser(String userId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with given user id"));
        //delete user profile image
        //images/user/abc.png
        String fullPath = imagePath + user.getImageName();
        logger.info("Image path : {} " + fullPath);

        try {
            Path path = Paths.get(fullPath);
            Files.delete(path);
        } catch (NoSuchFileException ex) {
            logger.info("No such file/directory exists");
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }


        userRepository.delete(user);


    }


    @Override
    public PageableResponse<UserDTO> getAllUsers(int pageNumber, int pageSize, String sortBy, String sortDir) {
        Sort sort = (sortDir.equalsIgnoreCase("desc")) ? (Sort.by(sortBy).descending()) : (Sort.by(sortBy).ascending());
//      pageNumber default value is 0
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<User> page = userRepository.findAll(pageable);
        PageableResponse<UserDTO> response = Helper.getPageableResponse(page, UserDTO.class);
        return response;
    }

    @Override
    public UserDTO getUserById(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with given user  id"));
        return entityToDto(user);
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found with given email id  !"));
        return entityToDto(user);
    }

    @Override
    public List<UserDTO> searchUser(String keyword) {
        List<User> users = userRepository.findByNameContaining(keyword);
        List<UserDTO> dtoList = users.stream().map(user -> entityToDto(user)).collect(Collectors.toList());
        return dtoList;
    }


    //coversion of dto->entity and entity->dto


    private User dtoToEntity(UserDTO userDTO) {
//        Manual creation of conversion of dto to entity
//        User user = User.builder()
//                .userId(userDTO.getUserId())
//                .name(userDTO.getName())
//                .email(userDTO.getEmail())
//                .password(userDTO.getPassword())
//                .gender(userDTO.getGender())
//                .about(userDTO.getAbout())
//                .imageName(userDTO.getImageName())
//                .build();


//        Automatic creation of conversion of dto to entity by model mapper


        return mapper.map(userDTO, User.class);
    }


    private UserDTO entityToDto(User user) {
//    Manual creation of conversion of entity to dto
//        UserDTO userDTO = UserDTO.builder()
//                .userId(user.getUserId())
//                .name(user.getName())
//                .email(user.getEmail())
//                .password(user.getPassword())
//                .gender(user.getGender())
//                .about(user.getAbout())
//                .imageName(user.getImageName())
//                .build();

//        Automatic creation of conversion of entity to dto by model mapper


        return mapper.map(user, UserDTO.class);
    }
}
