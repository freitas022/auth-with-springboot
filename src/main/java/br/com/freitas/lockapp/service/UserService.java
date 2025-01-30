package br.com.freitas.lockapp.service;

import br.com.freitas.lockapp.dto.RequestDto;
import br.com.freitas.lockapp.dto.UserDto;
import br.com.freitas.lockapp.exceptions.BusinessException;
import br.com.freitas.lockapp.exceptions.NotFoundException;
import br.com.freitas.lockapp.model.User;
import br.com.freitas.lockapp.model.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UserDto findById(final Long id) {
        return userRepository.findById(id)
                .map(UserDto::new)
                .orElseThrow(NotFoundException::new);
    }

    public List<UserDto> findAll() {
        var result = userRepository.findAll();
        return result.parallelStream()
                .map(UserDto::new)
                .toList();
    }

    public void save(RequestDto request) {
        existsByEmail(request.username());
        String password = new BCryptPasswordEncoder().encode(request.password());
        var userToSave = new User(null, request.username(), password, "CUSTOMER");
        userRepository.save(userToSave);
    }

    public void updatePassword(final Long id, final String newPassword) {
        String passwordToEncode = new BCryptPasswordEncoder().encode(newPassword);
        userRepository.findById(id)
                .map(found -> {
                    found.setPassword(passwordToEncode);
                    return userRepository.save(found);
                }).orElseThrow(NotFoundException::new);
    }

    public void existsByEmail(String email) {
        if (userRepository.existsByUsername(email)) {
            throw new BusinessException("Email already in use");
        }
    }
}
