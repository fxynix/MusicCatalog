package musiccatalog.dto.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;
import musiccatalog.model.Track;

@Data
public class GenreCreateDto {
    @NotNull
    private Long id;
    @NotBlank
    private String name;

    private List<Track> tracks;
}
