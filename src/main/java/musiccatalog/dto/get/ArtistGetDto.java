package musiccatalog.dto.get;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import musiccatalog.model.Album;
import musiccatalog.model.Artist;

@Getter
@Setter
public class ArtistGetDto {
    private Long id;
    private String name;
    private List<String> albums;

    public ArtistGetDto(Artist artist) {
        this.id = artist.getId();
        this.name = artist.getName();
        if (artist.getAlbums() != null) {
            this.albums = artist.getAlbums().stream()
                    .map(Album::getName)
                    .toList();
        }

    }

}
