package musiccatalog.dto.update;

import java.util.List;
import lombok.Data;

@Data
public class AlbumUpdateDto {

    private String name;

    private List<Long> artistsIds;

    private List<Long> tracksIds;
}
