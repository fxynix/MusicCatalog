package musiccatalog.dto.update;

import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlbumUpdateDto {

    private String name;

    private List<@Positive(message = "Album's artist(-s) id(-s) must be positive") Long> artistsIds;

    private List<@Positive(message = "Album's track(-s) id(-s) must be positive") Long> tracksIds;
}
