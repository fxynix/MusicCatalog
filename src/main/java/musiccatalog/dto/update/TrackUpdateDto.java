package musiccatalog.dto.update;

import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrackUpdateDto {

    private String name;
    @Positive
    private int duration;

    private List<@Positive Long> genresIds;

    private List<@Positive Long> likedByUsers;

    @Positive
    private Long albumId;

    private List<@Positive Long> playlistsIds;
}
