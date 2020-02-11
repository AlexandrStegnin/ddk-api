package com.ddkolesnik.ddkapi.controller;

import com.ddkolesnik.ddkapi.model.User;
import com.ddkolesnik.ddkapi.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Alexandr Stegnin
 */

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "api/v1/users")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserController {

    UserService userService;

    @GetMapping(path = "/{id}")
    public User findUser(@PathVariable long id) {
        return userService.findById(id);
    }

}
