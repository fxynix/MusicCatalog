package musiccatalog.dto.update;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Data;

@Data
public class UserUpdateDto {

    @Size(min = 4, max = 20, message = "Имя пользователя должно быть длиной от 4 до 20 символов")
    private String name;

    @Email(message = "Некорректный email")
    @Size(min = 4, max = 30, message = "Email должен быть длиной от 4 до 30 символов")
    private String email;

    @Size(min = 4, max = 20, groups = OnUpdatePassword.class,
            message = "Пароль должен быть длиной от 4 до 20 символов")
    @Null(groups = OnKeepPassword.class)
    private String password;

    public interface OnUpdatePassword {}

    public interface OnKeepPassword {}

    private List<@Positive(message =
            "ID созданных пользоватем плейлистов должны быть положительными") Long>
            createdPlaylistsIds;


    @Override
    public String toString() {
        return "UserUpdateDto{"
                + "name='" + name + '\''
                + ", email='" + email + '\''
                + ", pаssword='{PАSSWORD}'"
                + ", subscribedPlaylistsIds=" + createdPlaylistsIds
                + '}';
    }
}
