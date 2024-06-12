package br.com.freitas.lockapp.dto;

import br.com.freitas.lockapp.model.User;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public record UserDto(Long id, String name, String email, Boolean isEnabled, Collection<? extends GrantedAuthority> authorities) {

    public UserDto(User entity) {
        this(entity.getId(), entity.getName(), entity.getEmail(), entity.isEnabled(), entity.getAuthorities());
    }
}