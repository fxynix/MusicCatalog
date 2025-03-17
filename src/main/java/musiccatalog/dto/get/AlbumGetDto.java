package musiccatalog.dto.get;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import musiccatalog.model.Album;
import musiccatalog.model.Artist;
import musiccatalog.model.Track;

@Getter
@Setter
public class AlbumGetDto {
    private Long id;
    private String name;
    private List<String> artists;
    private List<String> tracks;

    public AlbumGetDto(Album album) {
        this.id = album.getId();
        this.name = album.getName();
        this.artists = album.getArtists().stream()
                .map(Artist::getName)
                .toList();
        this.tracks = album.getTracks().stream()
                .map(Track::getName)
                .toList();
    }

}