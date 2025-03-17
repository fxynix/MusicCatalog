package musiccatalog.dto.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;
import musiccatalog.model.Track;
import musiccatalog.model.User;

@Data
public class PlaylistCreateDto {
    @NotNull
    private Long id;
    @NotBlank
    private String name;

    private List<Track> tracks;
    @NotBlank
    private User author;

    private List<User> subscribers;
}
