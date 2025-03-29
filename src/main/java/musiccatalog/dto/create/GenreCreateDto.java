package musiccatalog.dto.create;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenreCreateDto {

    @NotBlank(message = "Имя создаваемого жанра не может быть пустым")
    private String name;

}
