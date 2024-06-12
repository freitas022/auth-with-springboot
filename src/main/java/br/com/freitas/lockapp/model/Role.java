package br.com.freitas.lockapp.model;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {

    CUSTOMER,
    DOCTOR,
    ADMIN;

    @Override
    public String getAuthority() {
        return "ROLE_" + name();
    }
}
