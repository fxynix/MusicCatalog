package musiccatalog.dto.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArtistCreateDto {

    @NotBlank(message = "Имя создаваемого исполнителя не может быть пустым")
    @Size(min = 4, max = 20, message = "Имя исполнителя должно быть длиной от 4 до 20 символов")
    private String name;

}
