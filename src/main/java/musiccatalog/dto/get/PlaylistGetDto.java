package musiccatalog.dto.get;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import musiccatalog.model.Playlist;
import musiccatalog.model.Track;

@Getter
@Setter
public class PlaylistGetDto {
    private Long id;
    private String name;
    private String author;
    private int playlistsCount;
    private List<String> tracks;

    public PlaylistGetDto(Playlist playlist) {
        this.id = playlist.getId();
        this.name = playlist.getName();
        this.author = playlist.getAuthor().getName();
        this.playlistsCount = playlist.getSubscribers().size();
        this.tracks = playlist.getTracks().stream()
                .map(Track::getName)
                .toList();
    }

}
