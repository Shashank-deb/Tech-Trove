package com.shashank.electronic.store.controllers;


import com.shashank.electronic.store.dtos.APIResponseMessage;
import com.shashank.electronic.store.dtos.ImageResponseMessage;
import com.shashank.electronic.store.dtos.PageableResponse;
import com.shashank.electronic.store.dtos.UserDTO;
import com.shashank.electronic.store.models.Providers;
import com.shashank.electronic.store.models.User;
import com.shashank.electronic.store.services.FileService;
import com.shashank.electronic.store.services.UserService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/users")
@Tag(name = "User Controller", description = "REST APIs related to perform user operations")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private FileService fileService;


    @Value("${user.profile.image.path}")
    private String imageUploadPath;

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    @PostMapping

    @Operation(summary = "Create a new user", description = "Creates a new user in the system and returns the created user details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<UserDTO> createUser(@Valid
                                              @RequestBody UserDTO userDTO) {

        userDTO.setProviders(Providers.SELF);
        UserDTO createdUserDTO = userService.createUser(userDTO);
        return new ResponseEntity<>(createdUserDTO, HttpStatus.CREATED);
    }


    @PutMapping("/{userId}")
    public ResponseEntity<UserDTO> updateUser(
            @Valid
            @PathVariable("userId") String userId,
            @RequestBody UserDTO userDTO
    ) {
        UserDTO updatedUserDTO = userService.updateUser(userDTO, userId);
        return new ResponseEntity<>(updatedUserDTO, HttpStatus.OK);
    }


    @DeleteMapping("/{userId}")
    public ResponseEntity<APIResponseMessage> deleteUser(@PathVariable("userId") String userId) {
        userService.deleteUser(userId);
        APIResponseMessage message = APIResponseMessage
                .builder()
                .message("User deleted successfully")
                .success(true)
                .status(HttpStatus.OK)
                .build();

        return new ResponseEntity<>(message, HttpStatus.OK);
    }


    @Operation(summary = "Get all Users", description = "This api is working on the basis of getting all the users from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Getting all users "),
            @ApiResponse(responseCode = "400", description = "Not able to find out the users")
    })
    @GetMapping
    public ResponseEntity<PageableResponse<UserDTO>> getAllUsers(

            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "name", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir


    ) {
        return new ResponseEntity<>(userService.getAllUsers(pageNumber, pageSize, sortBy, sortDir), HttpStatus.OK);
    }


    @GetMapping("/{userId}")

    @Operation(summary = "Get Single user by user id", description = "This api is working on the basis of getting a single user from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Getting single  users "),
            @ApiResponse(responseCode = "400", description = "Not able to find out the user")
    })
    public ResponseEntity<UserDTO> getUserById(@PathVariable("userId") String userId) {
        return new ResponseEntity<>(userService.getUserById(userId), HttpStatus.OK);
    }


    @GetMapping("/email/{email}")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable("email") String email) {
        return new ResponseEntity<>(userService.getUserByEmail(email), HttpStatus.OK);
    }


    @GetMapping("/search/{keyword}")
    public ResponseEntity<List<UserDTO>> searchUser(@PathVariable("keyword") String keyword) {
        return new ResponseEntity<>(userService.searchUser(keyword), HttpStatus.OK);
    }


    //    upload user image
    @PostMapping("/image/{userId}")
    public ResponseEntity<ImageResponseMessage> uploadUserImage(
            @RequestParam("userImage") MultipartFile image,
            @PathVariable("userId") String userId
    ) throws IOException {
        String imageName = fileService.uploadImage(image, imageUploadPath);
        UserDTO user = userService.getUserById(userId);
        user.setImageName(imageName);
        UserDTO userDTO = userService.updateUser(user, userId);

        ImageResponseMessage imageResponseMessage = ImageResponseMessage
                .builder()
                .imageName(imageName)
                .message("Image uploaded successfully")
                .success(true)
                .status(HttpStatus.CREATED)
                .build();
        return new ResponseEntity<>(imageResponseMessage, HttpStatus.CREATED);
    }


    //    server user image
    @GetMapping("/image/{userId}")
    public void serveUserImage(
            @PathVariable("userId") String userId,
            HttpServletResponse response
    ) throws IOException {
        UserDTO user = userService.getUserById(userId);
        logger.info("User Image Name: {} " + user.getImageName());
        InputStream resource = fileService.getResource(imageUploadPath, user.getImageName());
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        StreamUtils.copy(resource, response.getOutputStream());

    }


}
