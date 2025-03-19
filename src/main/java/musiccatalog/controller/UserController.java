package musiccatalog.controller;

import jakarta.validation.Valid;
import java.util.List;
import musiccatalog.dto.create.UserCreateDto;
import musiccatalog.dto.get.UserGetDto;
import musiccatalog.dto.update.UserUpdateDto;
import musiccatalog.model.User;
import musiccatalog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserGetDto>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users.stream()
                .map(UserGetDto::new)
                .toList());
    }

    @GetMapping("{id}")
    public ResponseEntity<UserGetDto> getUserById(@PathVariable long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        return ResponseEntity.ok(new UserGetDto(user));
    }

    @GetMapping(params = "name")
    public ResponseEntity<UserGetDto> getUserByName(@RequestParam String name) {
        User user = userService.getUserByName(name);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User was not found");
        }
        return ResponseEntity.ok(new UserGetDto(user));
    }

    @PostMapping
    public ResponseEntity<UserGetDto> createUser(@Valid @RequestBody UserCreateDto userDto) {
        User newUser = userService.createUser(userDto);
        return new ResponseEntity<>(new UserGetDto(newUser), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserGetDto> updateUser(@PathVariable Long id,
                                                 @Valid @RequestBody UserUpdateDto userDto) {
        User updatedUser = userService.updateUser(id, userDto);
        return ResponseEntity.ok(new UserGetDto(updatedUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<User> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}