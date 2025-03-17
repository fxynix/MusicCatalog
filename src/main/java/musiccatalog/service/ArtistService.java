package musiccatalog.service;

import java.util.List;
import musiccatalog.dto.create.ArtistCreateDto;
import musiccatalog.dto.update.ArtistUpdateDto;
import musiccatalog.model.Artist;
import org.springframework.stereotype.Service;

@Service
public interface ArtistService {
    List<Artist> getAllArtists();

    Artist getArtistById(long id);

    Artist getArtistByName(String name);

    Artist createArtist(ArtistCreateDto artistDto);

    Artist updateArtist(long id, ArtistUpdateDto artistDto);

    void deleteArtist(Long id);
}