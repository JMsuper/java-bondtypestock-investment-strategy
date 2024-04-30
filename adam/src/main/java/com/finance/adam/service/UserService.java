package com.finance.adam.service;

import com.finance.adam.auth.dto.AccountDto;
import com.finance.adam.dto.UserUpdateEmailDTO;
import com.finance.adam.dto.UserUpdatePasswordDTO;
import com.finance.adam.exception.CustomException;
import com.finance.adam.exception.ErrorCode;
import com.finance.adam.repository.UserRepository;
import com.finance.adam.repository.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public void saveUser(String id, String email, String password) {
        Optional<Account> optionalAccount = userRepository.findById(id);
        if(optionalAccount.isPresent()){
            throw new CustomException(ErrorCode.DUP_USER_ID);
        }
        String encodedPassword = "{noop}" + password;
        Account newAccount = new Account(id, email, encodedPassword, "ROLE_USER");
        userRepository.save(newAccount);
    }

    public AccountDto updateUserEmail(UserUpdateEmailDTO userUpdateEmailDTO) {
        Optional<Account> optionalAccount = userRepository.findById(userUpdateEmailDTO.getId());
        if(!optionalAccount.isPresent()){
            throw new CustomException(ErrorCode.NOT_FOUND_USER);
        }
        Account account = optionalAccount.get();
        account.setEmail(userUpdateEmailDTO.getEmail());
        userRepository.save(account);
        return new AccountDto(account.getId(), account.getEmail(),null,account.getRoles());
    }

    public AccountDto updateUserPassword(UserUpdatePasswordDTO userUpdatePasswordDTO) {
        Optional<Account> optionalAccount = userRepository.findById(userUpdatePasswordDTO.getId());
        if(!optionalAccount.isPresent()){
            throw new CustomException(ErrorCode.NOT_FOUND_USER);
        }
        Account account = optionalAccount.get();
        account.setPassword("{noop}" + userUpdatePasswordDTO.getPassword());
        userRepository.save(account);
        return new AccountDto(account.getId(), account.getEmail(),null,account.getRoles());
    }
}
