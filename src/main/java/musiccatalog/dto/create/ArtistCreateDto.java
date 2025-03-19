package musiccatalog.dto.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ArtistCreateDto {
    @NotBlank(message = "Artist's name can't be blank")
    private String name;

    private List<@Positive(message = "Artist's album(-s) id(-s) must be positive") Long> albumsIds;

}
