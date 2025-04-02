package musiccatalog.dto.create;

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
public class AlbumCreateDto {

    @NotBlank(message = "Имя создаваемого альбома не может быть пустым")
    @Size(min = 1, max = 20, message = "Название альбома должно быть длиной от 1 до 20 символов")
    private String name;

    @NotEmpty(message = "У альбома должен быть хотя бы 1 исполнитель")
    @NotNull(message =  "Создаваемый альбом не может быть без исполнителя")
    private List<@Positive(message =
            "ID исполнителя(-ей) должны быть положительными") Long> artistsIds;
}
