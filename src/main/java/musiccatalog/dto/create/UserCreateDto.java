package musiccatalog.dto.create;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateDto {
    @NotBlank(message = "User's name can't be blank")
    private String name;

    @NotBlank(message = "User's email can't be blank")
    private String email;

    @NotBlank(message = "User's password can't be blank")
    private String password;

}
