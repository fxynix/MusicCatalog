package musiccatalog.dto.update;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.Data;
import musiccatalog.model.Album;
import musiccatalog.model.Genre;
import musiccatalog.model.User;

@Data
public class TrackUpdateDto {
    @NotNull
    private Long id;

    private String name;
    @Positive
    private int duration;

    private List<Genre> genres;

    private List<User> likedByUsers;
    @Positive
    private int trackNumber;

    private Album album;
}
