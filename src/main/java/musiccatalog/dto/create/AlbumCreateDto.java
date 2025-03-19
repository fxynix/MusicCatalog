package musiccatalog.dto.create;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlbumCreateDto {
    @NotBlank(message = "Album's name can't be blank")
    private String name;
    @NotBlank(message =  "Album can't be without artist(-s)")
    private List<Long> artistsIds;
}
