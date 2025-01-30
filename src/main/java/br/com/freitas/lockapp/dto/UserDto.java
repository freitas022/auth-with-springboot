package br.com.freitas.lockapp.dto;

import br.com.freitas.lockapp.model.User;

public record UserDto(Long id, String username, String password) {

    public UserDto(User entity) {
        this(entity.getId(), entity.getUsername(), entity.getPassword());
    }
}