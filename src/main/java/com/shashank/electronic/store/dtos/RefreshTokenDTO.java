package com.shashank.electronic.store.dtos;


import jakarta.persistence.OneToOne;
import lombok.*;

import java.time.Instant;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class RefreshTokenDTO {

    private int id;
    private String token;
    private Instant expiryDate;


}
