package hr.fer.masters.project.service;

import hr.fer.masters.project.domain.entities.UserEntity;
import hr.fer.masters.project.domain.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Optional;

@AllArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserEntity getUserBySlackUserId(String slackUserId) {
        return userRepository.findByUsername(slackUserId).orElseThrow(() ->
                new NoSuchElementException()
        );
    }

    public UserEntity create(String slackUserId) {
        Optional<UserEntity> existingUser = userRepository.findByUsername(slackUserId);
        boolean userExists = existingUser.isPresent();
        if(!userExists) {
            UserEntity user = new UserEntity(slackUserId);
            var savedEntity = userRepository.save(user);
            return savedEntity;
        }
        return existingUser.get();
    }

    public UserEntity updateWorkingHoursStart(String username, String workingHoursStart) {
        UserEntity user = getUserBySlackUserId(username);
        user.setWorkingHoursStart(workingHoursStart);
        return userRepository.save(user);
    }

    public UserEntity updateWorkingHoursEnd(String username, String workingHoursEnd) {
        UserEntity user = getUserBySlackUserId(username);
        user.setWorkingHoursEnd(workingHoursEnd);
        return userRepository.save(user);
    }

    public UserEntity updateWorkingHours(String username, String workingHoursStart, String workingHoursEnd) throws ParseException {
        UserEntity user = getUserBySlackUserId(username);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date d1 = sdf.parse(workingHoursStart);
        Date d2 = sdf.parse(workingHoursEnd);
        long elapsed = d2.getTime() - d1.getTime();
        if (elapsed > 0) {
            user.setWorkingHoursStart(workingHoursStart);
            user.setWorkingHoursEnd(workingHoursEnd);
            return userRepository.save(user);
        }
        return user;
    }

    public UserEntity updateUsername(String slackUsersId, String username) {
        UserEntity user = getUserBySlackUserId(slackUsersId);
        user.setSlackUsername(username);
        return userRepository.save(user);
    }
}
