package musiccatalog.dto.update;

import java.util.List;
import lombok.Data;

@Data
public class UserUpdateDto {

    private String name;

    private String email;

    private String password;

    private List<Long> playlistsIds;

    private List<Long> likedTracksIds;

    private List<Long> likedArtistsIds;
}
