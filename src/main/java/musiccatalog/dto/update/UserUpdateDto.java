package musiccatalog.dto.update;

import jakarta.validation.constraints.Email;
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

    @Size(min = 4, max = 20, message = "Пароль должен быть длиной от 4 до 20 символов")
    private String password;

    private List<@Positive(message =
            "ID понравившихся пользователю плейлистов должны быть положительными") Long>
            subscribedPlaylistsIds;

    private List<@Positive(message =
            "ID понравившихся пользователю треков должны быть положительными") Long>
            likedTracksIds;
}
