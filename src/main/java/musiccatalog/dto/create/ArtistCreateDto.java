package musiccatalog.dto.create;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ArtistCreateDto {

    @NotBlank(message = "Имя создаваемого исполнителя не может быть пустым")
    private String name;

}
