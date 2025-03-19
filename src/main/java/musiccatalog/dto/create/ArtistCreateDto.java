package musiccatalog.dto.create;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ArtistCreateDto {
    @NotBlank(message = "Artist's name can't be blank")
    private String name;

    private List<Long> albumsIds;

}
