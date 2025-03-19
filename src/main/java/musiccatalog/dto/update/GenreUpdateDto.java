package musiccatalog.dto.update;

import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenreUpdateDto {

    private String name;

    private List<@Positive(message = "Genre's track(-s) id(-s) must be positive") Long> tracksIds;
}
