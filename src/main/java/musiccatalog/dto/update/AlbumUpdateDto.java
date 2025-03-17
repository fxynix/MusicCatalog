package musiccatalog.dto.update;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;
import musiccatalog.model.Artist;
import musiccatalog.model.Track;

@Data
public class AlbumUpdateDto {
    @NotNull
    private Long id;

    private String name;

    private List<Artist> artists;

    private List<Track> tracks;
}
