package musiccatalog.dto.update;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;
import musiccatalog.model.Track;

@Data
public class GenreUpdateDto {
    @NotNull
    private Long id;

    private String name;

    private List<Track> tracks;
}
