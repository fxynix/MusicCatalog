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

    private List<Long> genresIds;

    private List<Long> likedByUsers;

    private Long albumId;

    private List<Long> playlistsIds;
}
