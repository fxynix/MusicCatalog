package musiccatalog.dto.update;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;
import musiccatalog.model.Album;
import musiccatalog.model.User;

@Data
public class ArtistUpdateDto {
    @NotNull
    private Long id;

    private String name;

    private List<Album> albums;

    private List<User> likedByUsers;
}
