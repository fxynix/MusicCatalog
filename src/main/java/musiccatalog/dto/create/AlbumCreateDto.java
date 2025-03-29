package musiccatalog.dto.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlbumCreateDto {

    @NotBlank(message = "Имя создаваемого альбома не может быть пустым")
    private String name;

    @NotNull(message =  "Создаваемый альбом не может быть без исполнителя")
    private List<@Positive(message =
            "ID исполнителя(-ей) должны быть положительными") Long> artistsIds;
}
