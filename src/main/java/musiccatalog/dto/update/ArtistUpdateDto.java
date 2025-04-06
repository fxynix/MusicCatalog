package musiccatalog.dto.update;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArtistUpdateDto {

    @Size(min = 4, max = 20, message = "Имя исполнителя должно быть длиной от 4 до 20 символов")
    private String name;

    private List<@Positive(message =
            "ID альбомов исполнителя должны быть положительными") Long> albumsIds;

    @Override
    public String toString() {
        return "ArtistUpdateDto{"
                + "name='" + name + '\''
                + ", albumsIds=" + albumsIds
                + '}';
    }
}
