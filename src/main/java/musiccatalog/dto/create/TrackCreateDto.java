package musiccatalog.dto.create;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrackCreateDto {

    @NotBlank(message = "Поле имя создаваемого трека не может быть пустым")
    @Size(min = 1, max = 20, message = "Имя трека должно быть длиной от 1 до 20 символов")
    private String name;

    @Positive(message = "Длительность трека должна быть положительной")
    @NotNull(message = "Поле длительность трека не может быть пустым")
    @Min(value = 10, message = "Создаваемый трек не может длиться меньше 10 секунд")
    @Max(value = 3600, message = "Создаваемый трек не может длиться дольше часа")
    private Integer duration;

    @NotNull(message = "Поле ID альбома не может быть пустым")
    @Positive(message = "ID альбома должен быть положительным")
    private Long albumId;

    @NotEmpty(message = "Трек должен иметь хоть 1 жанр")
    private List<@Positive(message = "ID жанра должно быть положительным") Long> genresIds;

    @Override
    public String toString() {
        return "TrackCreateDto{"
                + "name='" + name + '\''
                + ", duration=" + duration
                + ", albumId=" + albumId
                + ", genresIds=" + genresIds
                + '}';
    }
}
