package musiccatalog.service;

import java.util.ArrayList;
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

        if (artistDto.getName() != null) {
            artist.setName(artistDto.getName());
        }
        List<Album> currentAlbums = new ArrayList<>(artist.getAlbums());

        List<Album> newAlbums = new ArrayList<>();
        if (artistDto.getAlbumsIds() != null) {
            for (Long albumId : artistDto.getAlbumsIds()) {
                Album album = albumRepository.findById(albumId)
                        .orElseThrow(() -> new NotFoundException("Альбом не найден"));

                if (!album.getArtists().contains(artist)) {
                    album.getArtists().add(artist);
                    albumRepository.save(album);
                }

                if (!newAlbums.contains(album)) {
                    newAlbums.add(album);
                }
            }
        }

        List<Album> albumsToRemove = currentAlbums.stream()
                .filter(album -> !newAlbums.contains(album))
                .toList();

        for (Album album : albumsToRemove) {
            album.getArtists().remove(artist);
            albumRepository.save(album);
        }

        artist.setAlbums(newAlbums);
        cache.clear();
        return artistRepository.save(artist);

    }

    public void deleteArtist(Long id) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("Не найдено исполнителя с ID = " + id));
        for (Album album : artist.getAlbums()) {
            album.getArtists().remove(artist);
            albumRepository.save(album);
        }
        artistRepository.delete(artist);
        cache.clear();
    }

}
