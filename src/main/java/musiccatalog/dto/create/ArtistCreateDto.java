package musiccatalog.dto.create;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Data;

@Data
public class ArtistCreateDto {
    @NotBlank
    private String name;

    private List<Long> albumsIds;

    private List<Long> likedByUsersIds;
}
