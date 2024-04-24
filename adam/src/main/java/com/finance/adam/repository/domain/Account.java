package com.finance.adam.repository.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Data
@NoArgsConstructor
public class Account implements Serializable {

    @Id
    private String id;
    private String email;
    private String password;
    private String roles;

    public Account(String id, String email, String password, String roleUser) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.roles = roleUser;
    }
}
