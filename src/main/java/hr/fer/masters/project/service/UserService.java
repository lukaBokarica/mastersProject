package hr.fer.masters.project.service;

import hr.fer.masters.project.domain.entities.UserEntity;
import hr.fer.masters.project.domain.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@AllArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserEntity getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() ->
                new NoSuchElementException()
        );
    }
}
