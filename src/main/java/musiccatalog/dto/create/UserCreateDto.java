package musiccatalog.dto.create;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateDto {

    @NotBlank(message = "Имя создаваемого пользователя не может быть пустым")
    @Size(min = 4, max = 20, message = "Имя пользователя должно быть длиной от 4 до 20 символов")
    private String name;

    @Email(message = "Некорректный email")
    @NotBlank(message = "Почта создаваемого пользователя не может быть пустым")
    @Size(min = 4, max = 30, message = "Email должен быть длиной от 4 до 30 символов")
    private String email;

    @NotBlank(message = "Пароль создаваемого пользователя не может быть пустым")
    @Size(min = 4, max = 20, message = "Пароль должен быть длиной от 4 до 20 символов")
    private String password;
}
