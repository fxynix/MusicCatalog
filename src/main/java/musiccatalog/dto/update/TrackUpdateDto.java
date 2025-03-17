package musiccatalog.dto.update;

import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.Data;
import musiccatalog.model.User;

@Data
public class TrackUpdateDto {

    private String name;
    @Positive
    private int duration;

    private List<Long> genresIds;

    private List<User> likedByUsers;
    @Positive
    private int trackNumber;

    private Long albumId;
}
