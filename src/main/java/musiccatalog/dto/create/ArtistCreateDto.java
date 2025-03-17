package musiccatalog.dto.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;
import musiccatalog.model.Album;
import musiccatalog.model.User;

@Data
public class ArtistCreateDto {
    @NotNull
    private Long id;
    @NotBlank
    private String name;

    private List<Album> albums;

    private List<User> likedByUsers;
}
