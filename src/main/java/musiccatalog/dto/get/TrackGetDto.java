package musiccatalog.dto.get;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import musiccatalog.model.Genre;
import musiccatalog.model.Track;

@Getter
@Setter
public class TrackGetDto {
    private Long id;
    private String name;
    private Integer duration;
    private int trackNumber;
    private String albumName;
    private List<String> genres;

    public TrackGetDto(Track track) {
        this.id = track.getId();
        this.name = track.getName();
        this.duration = track.getDuration();
        this.trackNumber = track.getTrackNumber();
        this.albumName = track.getAlbum().getName();
        this.genres = track.getGenres().stream()
                .map(Genre::getName)
                .toList();
    }

}