package musiccatalog.dto.update;

import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaylistUpdateDto {

    private String name;

    private List<@Positive(message =
            "ID треков плейлиста должы быть положительными") Long> tracksIds;

    @Positive(message = "ID автора плейлиста должно быть положительным")
    private Long authorId;

}
