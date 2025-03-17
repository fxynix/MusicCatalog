package musiccatalog.dto.create;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Data;

@Data
public class UserCreateDto {
    @NotBlank
    private String name;
    @NotBlank
    private String email;
    @NotBlank
    private String password;

    private List<Long> playlistsIds;

    private List<Long> likedTracksIds;

    private List<Long> likedArtistsIds;
}
