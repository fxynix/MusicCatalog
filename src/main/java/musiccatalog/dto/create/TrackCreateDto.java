package musiccatalog.dto.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrackCreateDto {
    @NotBlank(message = "Track's name can't be blank")
    private String name;

    @Positive(message = "Track's duration must be positive")
    private int duration;

    @NotBlank(message = "Track can't be without album")
    @Positive(message = "Track's album id must be positive")
    private Long albumId;

    @NotEmpty(message = "Track must have genre(-s)")
    private List<@Positive Long> genresIds;
}
