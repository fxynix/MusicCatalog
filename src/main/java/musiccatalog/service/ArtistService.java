package musiccatalog.service;

import java.util.List;
import java.util.Optional;
import musiccatalog.cache.InMemoryCache;
import musiccatalog.dto.create.ArtistCreateDto;
import musiccatalog.dto.update.ArtistUpdateDto;
import musiccatalog.exception.NotFoundException;
import musiccatalog.model.Album;
import musiccatalog.model.Artist;
import musiccatalog.repository.AlbumRepository;
import musiccatalog.repository.ArtistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ArtistService {

    private final ArtistRepository artistRepository;
    private final AlbumRepository albumRepository;
    private final InMemoryCache cache;

    @Autowired
    public ArtistService(ArtistRepository artistRepository, AlbumRepository albumRepository,
                         InMemoryCache cache) {
        this.artistRepository = artistRepository;
        this.albumRepository = albumRepository;
        this.cache = cache;
    }

    public List<Artist> getAllArtists() {
        return artistRepository.findAll();
    }

    public Optional<Artist> getArtistById(long id) {
        String cacheKey = "artists_id_" + id;
        if (cache.containsKey(cacheKey)) {
            return (Optional.of(((Artist) cache.get(cacheKey))));
        }
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Не найдено исполнителя с ID " + id));
        cache.put(cacheKey, artist);
        return Optional.of(artist);
    }

    public Artist getArtistByName(String name)  {
        String cacheKey = "artists_name_" + name;
        if (cache.containsKey(cacheKey)) {
            return (Artist) cache.get(cacheKey);
        }
        Artist artist = artistRepository.findArtistByName(name);
        cache.put(cacheKey, artist);
        return artist;
    }

    public Artist createArtist(ArtistCreateDto artistDto) {
        Artist artist = new Artist();
        artist.setName(artistDto.getName());
        cache.clear();
        return artistRepository.save(artist);
    }

    public Artist updateArtist(long id, ArtistUpdateDto artistDto) {
        Artist artist = getArtistById(id)
                .orElseThrow(() -> new NotFoundException("Не найдено исполнителя с ID " + id));
        if (artistDto.getAlbumsIds() != null) {
            artist.setAlbums(albumRepository.findAllById(artistDto.getAlbumsIds()));
        }
        if (artistDto.getName() != null) {
            artist.setName(artistDto.getName());
        }
        List<Album> albums;
        if (artistDto.getAlbumsIds() != null) {
            albums = artistDto.getAlbumsIds().stream().map(albumId ->
                    albumRepository.findById(albumId)
                        .orElseThrow(() ->
                                new NotFoundException(
                                     "Не найдено указанного альбома исполнителя"))).toList();
            artist.setAlbums(albums);
        }
        cache.clear();
        return artistRepository.save(artist);

    }

    public void deleteArtist(Long id) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("Не найдено исполнителя с ID = " + id));
        artistRepository.delete(artist);
        cache.clear();
    }

}
