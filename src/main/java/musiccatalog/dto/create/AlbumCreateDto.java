package musiccatalog.dto.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlbumCreateDto {
    @NotBlank(message = "Album's name can't be blank")
    private String name;
    @NotNull(message =  "Album can't be without artist(-s)")
    private List<@Positive(message = "Artist's id(-s) must be positive") Long> artistsIds;
}
