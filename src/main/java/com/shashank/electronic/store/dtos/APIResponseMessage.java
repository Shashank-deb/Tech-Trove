package com.shashank.electronic.store.dtos;

import lombok.*;
import org.springframework.http.HttpStatus;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class APIResponseMessage {

    private String message;
    private boolean success;
    private HttpStatus status;


}
