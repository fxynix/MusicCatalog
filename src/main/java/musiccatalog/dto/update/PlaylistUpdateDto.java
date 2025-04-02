package musiccatalog.dto.update;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaylistUpdateDto {

    @Size(min = 1, max = 20, message = "Название плейлиста должно быть длиной от 1 до 20 символов")
    private String name;

    private List<@Positive(message =
            "ID треков плейлиста должы быть положительными") Long> tracksIds;

    @Positive(message = "ID автора плейлиста должно быть положительным")
    private Long authorId;

}
