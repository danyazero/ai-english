package org.zero.aienglish.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.zero.aienglish.entity.User;
import org.zero.aienglish.exception.RequestException;
import org.zero.aienglish.model.UserDTO;
import org.zero.aienglish.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Integer createUser(UserDTO user) {
        var founded = userRepository.findFirstByUsername(user.username());
        if (founded.isPresent()) {
            log.warn("User already exist with id -> {}", founded.get().getId());
            return founded.get().getId();
        }

        var newUser = User.builder()
                .username(user.username())
                .role("USER")
                .build();

        return userRepository.save(newUser).getId();
    }

    public Integer updateUser(Integer userId, UserDTO user) {
        var founded = userRepository.findById(userId);
        if (founded.isEmpty()) {
            log.warn("User does not exist with id -> {}", userId);
            throw new RequestException("User does not exist with id -> " + userId);
        }
        founded.get().setUsername(user.username());
        return userRepository.save(founded.get()).getId();
    }
}
