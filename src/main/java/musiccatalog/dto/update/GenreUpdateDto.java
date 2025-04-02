package musiccatalog.dto.update;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenreUpdateDto {

    @Size(min = 2, max = 20, message = "Название жанра должно быть длиной от 2 до 20 символов")
    private String name;

}
