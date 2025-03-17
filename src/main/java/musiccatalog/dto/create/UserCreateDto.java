package musiccatalog.dto.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;
import musiccatalog.model.Artist;
import musiccatalog.model.Playlist;
import musiccatalog.model.Track;

@Data
public class UserCreateDto {
    @NotNull
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String email;
    @NotBlank
    private String password;

    private List<Playlist> playlists;

    private List<Track> likedTracks;

    private List<Artist> likedArtists;
}
