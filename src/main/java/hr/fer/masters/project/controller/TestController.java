package hr.fer.masters.project.controller;

import hr.fer.masters.project.domain.entities.UserEntity;
import hr.fer.masters.project.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/")
public class TestController {

    private UserService userService;

    @GetMapping("/")
    public UserEntity getUser() {
        return userService.getUserByUsername("veki");
    }
}
