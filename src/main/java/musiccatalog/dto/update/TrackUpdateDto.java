package musiccatalog.dto.update;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrackUpdateDto {

    @Size(min = 1, max = 20, message = "Имя трека должно быть длиной от 1 до 20 символов")
    private String name;

    @Positive(message = "Длительность трека должна быть положительной")
    @Min(value = 10, message = "Длительность трека не может быть меньше 10 секунд")
    @Max(value = 3600, message = "Длительность трека не может быть больше 1 часа")
    private Integer duration;

    @NotEmpty(message = "Трек должен иметь хоть 1 жанр")
    private List<@Positive(message =
            "ID жанров, к которым отнисится трек, должны быть положительными") Long> genresIds;

    @Positive(message = "Трек должен относиться хоть к какому-то альбому")
    private Long albumId;

    @Override
    public String toString() {
        return "TrackUpdateDto{"
                + "name='" + name + '\''
                + ", duration=" + duration
                + ", genresIds=" + genresIds
                + ", albumId=" + albumId
                + '}';
    }
}
