package musiccatalog.dto.update;

import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaylistUpdateDto {

    private String name;

    @Positive(message = "Playlist's track(-s) id(-s) must be positive")
    private List<Long> tracksIds;

    @Positive(message = "Playlist's author id must be positive")
    private Long authorId;

    @Positive(message = "Playlist's subscriber(-s) id(-s) must be positive")
    private List<Long> subscribersIds;
}
