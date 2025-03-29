package musiccatalog.dto.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrackCreateDto {

    @NotBlank(message = "Имя создаваемого трека не может быть пустым")
    private String name;

    @Positive(message = "Длительность трека должна быть положительной")
    private int duration;

    @Positive(message = "ID альбома должен быть положительным")
    private Long albumId;

    @NotEmpty(message = "Трек должен иметь хоть 1 жанр")
    private List<@Positive(message = "ID жанра должно быть положительным") Long> genresIds;
}
