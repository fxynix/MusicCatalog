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
    private List<String> playlistsCreated;
    private List<String> playlistsLiked;
    private int likedArtistsCount;
    private int likedTracksCount;

    public UserGetDto(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.playlistsCreated = user.getPlaylistsCreated().stream()
                .map(Playlist::getName)
                .toList();
        this.playlistsLiked = user.getPlaylistsSubscribed().stream()
                .map(Playlist::getName)
                .toList();
        this.likedTracksCount = user.getLikedTracks().size();
    }

}
