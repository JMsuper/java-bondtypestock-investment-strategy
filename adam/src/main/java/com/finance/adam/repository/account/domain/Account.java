package com.finance.adam.repository.account.domain;

import com.finance.adam.repository.savecorpinfo.domain.SaveCorpInfo;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Account implements Serializable {

    @Id
    private String id;
    private String email;
    private String password;
    private String roles;

    @OneToMany(mappedBy = "account")
    private List<SaveCorpInfo> saveCorpInfoList;

    public Account(String id, String email, String password, String roleUser) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.roles = roleUser;
    }
}
