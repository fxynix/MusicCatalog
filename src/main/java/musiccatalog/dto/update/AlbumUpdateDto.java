package musiccatalog.dto.update;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlbumUpdateDto {

    @Size(min = 1, max = 20, message = "Название альбома должно быть длиной от 1 до 20 символов")
    private String name;

    private List<@Positive(message =
            "ID исполнителей альбома должны быть положительными") Long> artistsIds;

    private List<@Positive(message =
            "ID треков альбома должны быть положительными") Long> tracksIds;

    @Override
    public String toString() {
        return "AlbumUpdateDto{"
                + "name='" + name + '\''
                + ", artistsIds=" + artistsIds
                + ", tracksIds=" + tracksIds
                + '}';
    }
}
