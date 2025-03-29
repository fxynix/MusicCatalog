package musiccatalog.dto.update;

import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlbumUpdateDto {

    private String name;

    private List<@Positive(message =
            "ID исполнителей альбома должны быть положительными") Long> artistsIds;

    private List<@Positive(message =
            "ID треков альбома должны быть положительными") Long> tracksIds;
}
