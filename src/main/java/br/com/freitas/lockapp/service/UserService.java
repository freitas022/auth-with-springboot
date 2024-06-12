package br.com.freitas.lockapp.service;

import br.com.freitas.lockapp.dto.RequestDto;
import br.com.freitas.lockapp.dto.UserDto;
import br.com.freitas.lockapp.exceptions.BusinessException;
import br.com.freitas.lockapp.exceptions.NotFoundException;
import br.com.freitas.lockapp.model.User;
import br.com.freitas.lockapp.model.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

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
        existsByEmail(request.email());
        String password = new BCryptPasswordEncoder().encode(request.password());
        var userToSave = new User(request.name(), request.email(), password, "CUSTOMER");
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
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException("Email already in use");
        }
    }

    public void activateUser(UserDto userDto) {
        User user = userRepository.findById(userDto.id())
                .orElseThrow(NotFoundException::new);
        if (!user.isEnabled()) {
            //service de email para ativar o usuário
        }
    }
}
