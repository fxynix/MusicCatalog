package musiccatalog.dto.create;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Data;

@Data
public class GenreCreateDto {
    @NotBlank
    private String name;

    private List<Long> tracksIds;
}
