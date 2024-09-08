package com.shashank.electronic.store.dtos;



import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryDTO {

    private String categoryId;
    @NotBlank(message = "Title cannot be blank")
    @Size(min = 4, max = 100, message = "Title must be between 4 and 100 characters")
    private String title;
    @NotBlank(message = "Description cannot be blank")
    private String description;
    @NotBlank(message = "Cover image cannot be blank")
    private String coverImage;

}
