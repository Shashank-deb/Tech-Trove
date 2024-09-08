package com.shashank.electronic.store.dtos;


import com.shashank.electronic.store.models.Providers;
import com.shashank.electronic.store.models.Role;
import com.shashank.electronic.store.validate.ImageNameValid;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString

public class UserDTO {

    @Schema(description = "Unique identifier of the user", example = "1add93aer8se2345")
    private String userId;

    @Schema(description = "Name of the user", example = "JohnDoe")
    @Size(min = 4, max = 15, message = "Invalid Username")
    private String name;

    //    @Email(message = "Email should be valid")
    @Schema(description = "Email address of the user", example = "example@gmail.com")
    @Pattern(regexp = "^[a-zA-Z0-9+_.-]+@gmail\\.com$", message = "Invalid User Email")
    @NotBlank(message = "Email should not be blank")
    private String email;

    @Schema(description = "Password for the user account", example = "securePassword123")
    @NotBlank(message = "Password should not be blank")
    private String password;


    @Schema(description = "Gender of the user", example = "Male")
    @Size(min = 4, max = 6, message = "Invalid Gender")
    private String gender;

    @Schema(description = "Brief description about the user", example = "Software engineer with 5 years of experience.")
    @NotBlank(message = "About should not be blank")
    private String about;

    @Schema(description = "Roles assigned to the user")
    private List<RoleDTO> roles;

    @Schema(description = "Name of the user's profile image", example = "profile.jpg")
    @ImageNameValid
    private String imageName;

    @Schema(description = "Provider of the user account", example = "SELF")
    private Providers providers=Providers.SELF;


}
