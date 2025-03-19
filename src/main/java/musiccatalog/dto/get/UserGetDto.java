package musiccatalog.dto.get;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import musiccatalog.model.Playlist;
import musiccatalog.model.User;

@Getter
@Setter
public class UserGetDto {
    private Long id;
    private String name;
    private List<String> playlists;
    private int likedArtistsCount;
    private int likedTracksCount;

    public UserGetDto(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.playlists = user.getPlaylistsCreated().stream()
                .map(Playlist::getName)
                .toList();
        this.playlists = user.getPlaylistsSubscribed().stream()
                .map(Playlist::getName)
                .toList();
        this.likedTracksCount = user.getLikedTracks().size();
    }

}
