package musiccatalog.dto.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.Data;

@Data
public class TrackCreateDto {
    @NotBlank
    private String name;

    @Positive
    private int duration;

    @Positive
    private int trackNumber;

    @NotNull
    private Long albumId;

    @NotEmpty
    private List<Long> genresIds;
}
