package musiccatalog.dto.update;

import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrackUpdateDto {

    private String name;

    @Positive(message = "Длительность трека должна быть положительной")
    private int duration;

    private List<@Positive(message =
            "ID жанров, к которым отнисится трек, должны быть положительными") Long> genresIds;

    @Positive(message = "ID альбома, к которому отнисится трек, должен быть положительным")
    private Long albumId;

}
