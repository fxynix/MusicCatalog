package musiccatalog.dto.update;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;
import musiccatalog.model.Track;
import musiccatalog.model.User;

@Data
public class PlaylistUpdateDto {
    @NotNull
    private Long id;

    private String name;

    private List<Track> tracks;

    private User author;

    private List<User> subscribers;
}
