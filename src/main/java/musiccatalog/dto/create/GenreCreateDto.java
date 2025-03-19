package musiccatalog.dto.create;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenreCreateDto {
    @NotBlank(message = "Genre's name can't be blank")
    private String name;

}
