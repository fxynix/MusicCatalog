package musiccatalog.dto.create;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Data;

@Data
public class AlbumCreateDto {
    @NotBlank
    private String name;
    @NotBlank
    private List<Long> artistsIds;

    private List<Long> tracksIds;
}
