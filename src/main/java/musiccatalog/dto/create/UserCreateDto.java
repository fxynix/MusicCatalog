package musiccatalog.dto.create;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateDto {

    @NotBlank(message = "Имя создаваемого пользователя не может быть пустым")
    private String name;

    @NotBlank(message = "Почта создаваемого пользователя не может быть пустым")
    private String email;

    @NotBlank(message = "Пароль создаваемого пользователя не может быть пустым")
    private String password;
}
