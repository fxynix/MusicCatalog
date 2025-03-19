package musiccatalog.dto.update;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.Data;

@Data
public class UserUpdateDto {

    private String name;

    @Email(message = "Invalid email")
    private String email;

    private String password;

    @Positive(message = "User's subscribed playlist(-s) id(-s) must be positive")
    private List<Long> subscribedPlaylistsIds;

    @Positive(message = "User's created playlist(-s) id(-s) must be positive")
    private List<Long> createdPlaylistsIds;

    @Positive(message = "User's liked track(-s) id(-s) must be positive")
    private List<Long> likedTracksIds;
}
