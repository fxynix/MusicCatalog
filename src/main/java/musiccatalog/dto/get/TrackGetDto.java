package musiccatalog.dto.get;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import musiccatalog.model.Artist;
import musiccatalog.model.Genre;
import musiccatalog.model.Track;

@Getter
@Setter
public class TrackGetDto {
    private Long id;
    private String name;
    private Integer duration;
    private String albumName;
    private List<String> genres;
    private List<String> artists;

    public TrackGetDto(Track track) {
        this.id = track.getId();
        this.name = track.getName();
        this.duration = track.getDuration();
        this.albumName = track.getAlbum() != null ? track.getAlbum().getName() : null;
        this.genres = track.getGenres() != null
                ? track.getGenres().stream().map(Genre::getName).toList()
                : List.of();
        this.artists = track.getAlbum() != null && track.getAlbum().getArtists() != null
                ? track.getAlbum().getArtists().stream().map(Artist::getName).toList()
                : List.of();
    }

    @Override
    public String toString() {
        return "TrackGetDto{"
                + "id=" + id
                + ", name='" + name + '\''
                + ", duration=" + duration
                + ", albumName='" + albumName + '\''
                + ", genres=" + genres
                + ", artists=" + artists
                + '}';
    }

}