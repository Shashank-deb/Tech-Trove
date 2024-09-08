package com.shashank.electronic.store.services;

import com.shashank.electronic.store.dtos.PageableResponse;
import com.shashank.electronic.store.dtos.UserDTO;
import com.shashank.electronic.store.models.User;

import java.util.List;

public interface UserService {


    UserDTO createUser(UserDTO userDTO);

    UserDTO updateUser(UserDTO userDTO, String userId);


    void deleteUser(String userId);


    PageableResponse<UserDTO> getAllUsers(int pageNumber, int pageSize, String sortBy, String sortDir);

    UserDTO getUserById(String userId);


    UserDTO getUserByEmail(String email);


    List<UserDTO> searchUser(String keyword);


}
