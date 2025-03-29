package musiccatalog.dto.update;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.Data;

@Data
public class UserUpdateDto {

    private String name;

    @Email(message = "Некорректный email")
    private String email;

    private String password;

    private List<@Positive(message =
            "ID понравившихся пользователю плейлистов должны быть положительными") Long>
            subscribedPlaylistsIds;

    private List<@Positive(message =
            "ID понравившихся пользователю треков должны быть положительными") Long>
            likedTracksIds;
}
