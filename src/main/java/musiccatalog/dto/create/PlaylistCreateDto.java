package musiccatalog.dto.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaylistCreateDto {
    @NotBlank(message = "Playlist's name can't be blank")
    private String name;

    private List<@Positive(message = "Playlist's track(-s) id(-s) must be positive") Long>
            tracksIds;

    @NotNull(message = "Playlist can't be without creator")
    private Long authorId;

}
