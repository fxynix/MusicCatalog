package musiccatalog.dto.update;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;
import musiccatalog.model.Artist;
import musiccatalog.model.Playlist;
import musiccatalog.model.Track;

@Data
public class UserUpdateDto {
    @NotNull
    private Long id;

    private String name;

    private String email;

    private String password;

    private List<Playlist> playlists;

    private List<Track> likedTracks;

    private List<Artist> likedArtists;
}
