package musiccatalog.dto.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaylistCreateDto {

    @NotBlank(message = "Имя создаваемого плейлиста не может быть пустым")
    @Size(min = 1, max = 20, message = "Название плейлиста должно быть длиной от 1 до 20 символов")
    private String name;

    private List<@Positive(message =
            "ID треков должно быть положительными") Long> tracksIds;

    @NotNull(message = "Создаваемый плейлист не может не иметь автора")
    @Positive(message = "ID автора должно быть положительным")
    private Long authorId;

}
