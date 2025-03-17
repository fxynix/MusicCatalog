package musiccatalog.dto.update;

import java.util.List;
import lombok.Data;

@Data
public class PlaylistUpdateDto {

    private String name;

    private List<Long> tracksIds;

    private Long authorId;

    private List<Long> subscribersIds;
}
