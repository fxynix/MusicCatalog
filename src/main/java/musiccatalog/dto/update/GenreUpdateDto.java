package musiccatalog.dto.update;

import java.util.List;
import lombok.Data;

@Data
public class GenreUpdateDto {

    private String name;

    private List<Long> tracksIds;
}
