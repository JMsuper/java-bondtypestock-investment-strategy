package com.finance.adam.auth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finance.adam.auth.dto.AccountContext;
import com.finance.adam.auth.dto.AccountDto;
import com.finance.adam.repository.UserRepository;
import com.finance.adam.repository.domain.Account;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("userDetailsService")
@RequiredArgsConstructor
public class RestUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {

        Optional<Account> optionalAccount = userRepository.findById(id);
        if (!optionalAccount.isPresent()) {
            throw new UsernameNotFoundException("No user found with id: " + id);
        }
        Account account = optionalAccount.get();

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(account.getRoles()));
        ModelMapper mapper = new ModelMapper();
        AccountDto accountDto = mapper.map(account, AccountDto.class);

        return new AccountContext(accountDto, authorities);
    }
}
