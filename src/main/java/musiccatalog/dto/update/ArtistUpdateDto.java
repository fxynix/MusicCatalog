package musiccatalog.dto.update;

import java.util.List;
import lombok.Data;

@Data
public class ArtistUpdateDto {
    private String name;

    private List<Long> albumsIds;

    private List<Long> likedByUsersIds;
}
