package hr.fer.masters.project.service;

import hr.fer.masters.project.domain.entities.UserEntity;
import hr.fer.masters.project.domain.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.NoSuchElementException;
import java.util.Optional;

@AllArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserEntity getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() ->
                new NoSuchElementException()
        );
    }

    public UserEntity create(String username) {
        Optional<UserEntity> existingUser = userRepository.findByUsername(username);
        boolean userExists = existingUser.isPresent();
        if(!userExists) {
            UserEntity user = new UserEntity(username);
            var savedEntity = userRepository.save(user);
            return savedEntity;
        }
        return existingUser.get();
    }

    public UserEntity updateWorkingHoursStart(String username, String workingHoursStart) {
        UserEntity user = getUserByUsername(username);
        user.setWorkingHoursStart(workingHoursStart);
        return userRepository.save(user);
    }

    public UserEntity updateWorkingHoursEnd(String username, String workingHoursEnd) {
        UserEntity user = getUserByUsername(username);
        user.setWorkingHoursEnd(workingHoursEnd);
        return userRepository.save(user);
    }
}
