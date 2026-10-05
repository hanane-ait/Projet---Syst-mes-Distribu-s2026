package org.example.ebankservice.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.*;
import org.example.ebankservice.model.Customer;

import java.util.Date;
@Entity
@NoArgsConstructor @AllArgsConstructor @Getter @Setter @Builder
public class BankAccount {
    @Id
    private String id;
    private Date createdAt;
    private double balance;
    private String type ;
    private long customerId;
    @Transient
    private Customer customer;
}
