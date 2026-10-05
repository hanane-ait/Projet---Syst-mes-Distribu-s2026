package org.example.ebankservice.model;

import jakarta.persistence.Id;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Customer {
    private Long id;
    private String name;
    private String email;
}
