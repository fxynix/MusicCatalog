package musiccatalog.dto.get;

import lombok.Getter;
import lombok.Setter;
import musiccatalog.model.Genre;

@Getter
@Setter
public class GenreGetDto {
    private Long id;
    private String name;
    private int tracksCount;

    public GenreGetDto(Genre genre) {
        this.id = genre.getId();
        this.name = genre.getName();
        if (genre.getTracks() != null) {
            this.tracksCount = genre.getTracks().size();
        }
    }

    @Override
    public String toString() {
        return "GenreGetDto{"
                + "id=" + id
                + ", name='" + name + '\''
                + ", tracksCount=" + tracksCount
                + '}';
    }

}
