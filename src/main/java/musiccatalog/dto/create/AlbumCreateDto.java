package musiccatalog.dto.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;
import musiccatalog.model.Artist;
import musiccatalog.model.Track;

@Data
public class AlbumCreateDto {
    @NotNull
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private List<Artist> artists;

    private List<Track> tracks;
}
