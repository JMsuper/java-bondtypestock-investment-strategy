package com.finance.adam.service;

import com.finance.adam.auth.dto.AccountDto;
import com.finance.adam.repository.account.dto.UserUpdateEmailDTO;
import com.finance.adam.repository.account.dto.UserUpdatePasswordDTO;
import com.finance.adam.exception.CustomException;
import com.finance.adam.exception.ErrorCode;
import com.finance.adam.repository.account.UserRepository;
import com.finance.adam.repository.account.domain.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public void saveUser(String id, String email, String password) {
        log.info("Attempting to save new user with id: {}", id);
        Optional<Account> optionalAccount = userRepository.findById(id);
        if(optionalAccount.isPresent()){
            log.warn("User with id {} already exists", id);
            throw new CustomException(ErrorCode.DUP_USER_ID);
        }
        String encodedPassword = "{noop}" + password;
        Account newAccount = new Account(id, email, encodedPassword, "ROLE_USER");
        userRepository.save(newAccount);
        log.info("Successfully saved new user with id: {}", id);
    }

    public AccountDto updateUserEmail(UserUpdateEmailDTO userUpdateEmailDTO, String userId) {
        log.info("Attempting to update email for user: {}", userId);
        Optional<Account> optionalAccount = userRepository.findById(userId);
        if(!optionalAccount.isPresent()){
            log.warn("User not found with id: {}", userId);
            throw new CustomException(ErrorCode.NOT_FOUND_USER);
        }
        Account account = optionalAccount.get();
        account.setEmail(userUpdateEmailDTO.getEmail());
        userRepository.save(account);
        log.info("Successfully updated email for user: {}", userId);
        return new AccountDto(account.getId(), account.getEmail(),null,account.getRoles());
    }

    public AccountDto updateUserPassword(UserUpdatePasswordDTO userUpdatePasswordDTO, String userId) {
        log.info("Attempting to update password for user: {}", userId);
        Optional<Account> optionalAccount = userRepository.findById(userId);
        if(!optionalAccount.isPresent()){
            log.warn("User not found with id: {}", userId);
            throw new CustomException(ErrorCode.NOT_FOUND_USER);
        }
        Account account = optionalAccount.get();
        account.setPassword("{noop}" + userUpdatePasswordDTO.getPassword());
        userRepository.save(account);
        log.info("Successfully updated password for user: {}", userId);
        return new AccountDto(account.getId(), account.getEmail(),null,account.getRoles());
    }
}
