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
    private String email;
    private List<String> playlistsCreated;
    private List<String> playlistsLiked;
    private int likedTracksCount;

    public UserGetDto(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        if (user.getPlaylistsCreated() != null) {
            this.playlistsCreated = user.getPlaylistsCreated().stream()
                    .map(Playlist::getName)
                    .toList();
        }
        if (user.getPlaylistsSubscribed() != null) {
            this.playlistsLiked = user.getPlaylistsSubscribed().stream()
                    .map(Playlist::getName)
                    .toList();
        }
        if (user.getLikedTracks() == null) {
            this.likedTracksCount = 0;
        } else {
            this.likedTracksCount = user.getLikedTracks().size();
        }
    }

}
