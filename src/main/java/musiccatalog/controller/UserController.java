package musiccatalog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import musiccatalog.dto.create.UserCreateDto;
import musiccatalog.dto.get.UserGetDto;
import musiccatalog.dto.update.UserUpdateDto;
import musiccatalog.exception.NotFoundException;
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

@RestController
@Tag(name = "User Controller", description = "API для управления пользователями")
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Получить всех пользователей",
            description = "Возвращает все пользователей")
    @ApiResponse(responseCode = "200", description = "Пользователи найдены успешно")
    public ResponseEntity<List<UserGetDto>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users.stream()
                .map(UserGetDto::new)
                .toList());
    }

    @GetMapping("{id}")
    @Operation(summary = "Получить пользователя по ID",
            description = "Возвращает пользователя по указанному ID в базе данных")
    @ApiResponse(responseCode = "200", description = "Пользователь найден успешно")
    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    public ResponseEntity<UserGetDto> getUserById(
            @Parameter(description = "ID искомого пользователя", example = "1")
            @PathVariable long id) {
        User user = userService.getUserById(id)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с ID = " + id));
        return ResponseEntity.ok(new UserGetDto(user));
    }

    @GetMapping(params = "name")
    @Operation(summary = "Получить пользователя по имени",
            description = "Возвращает пользователя по указанному имени")
    @ApiResponse(responseCode = "200", description = "Пользователь найден успешно")
    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    public ResponseEntity<UserGetDto> getUserByName(
            @Parameter(description = "Имя искомого пользователя", example = "Вася1991")
            @RequestParam String name) {
        User user = userService.getUserByName(name);
        if (user == null) {
            throw new NotFoundException("Пользователей с указанным именем не найдено");
        }
        return ResponseEntity.ok(new UserGetDto(user));
    }

    @PostMapping
    @Operation(summary = "Создать нового пользователя",
            description = "Создаёт нового пользователя")
    @ApiResponse(responseCode = "200", description = "Пользователь создан успешно")
    @ApiResponse(responseCode = "400", description = "Некорректный ввод")
    public ResponseEntity<UserGetDto> createUser(@Valid @RequestBody UserCreateDto userDto) {
        User newUser = userService.createUser(userDto);
        return new ResponseEntity<>(new UserGetDto(newUser), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Обновить пользователя",
            description = "Обновляет информацию о существующем пользователе по ID")
    @ApiResponse(responseCode = "200", description = "Пользователь обновлён успешно")
    @ApiResponse(responseCode = "400", description = "Некорректный ввод")
    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    public ResponseEntity<UserGetDto> updateUser(
            @Parameter(description = "ID искомого обновляемого", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateDto userDto) {
        User updatedUser = userService.updateUser(id, userDto);
        return ResponseEntity.ok(new UserGetDto(updatedUser));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить пользователя",
            description = "Удаляет пользователя по ID")
    @ApiResponse(responseCode = "204", description = "Пользователь удалён успешно")
    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    public ResponseEntity<User> deleteUser(
            @Parameter(description = "ID удаляемого пользователя", example = "1")
            @PathVariable Long id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}