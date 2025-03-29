package musiccatalog.dto.update;

import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArtistUpdateDto {

    private String name;

    private List<@Positive(message =
            "ID альбомов исполнителя должны быть положительными") Long> albumsIds;
}
