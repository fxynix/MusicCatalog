package musiccatalog.service;

import java.util.List;
import musiccatalog.dto.create.ArtistCreateDto;
import musiccatalog.dto.update.ArtistUpdateDto;
import musiccatalog.model.Album;
import musiccatalog.model.Artist;
import musiccatalog.repository.AlbumRepository;
import musiccatalog.repository.ArtistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
        List<Artist> artists = artistRepository.findAll();
        String cacheKey = "artists_all";
        if (cache.containsKey(cacheKey)) {
            return (List<Artist>) cache.get(cacheKey);
        }
        cache.put(cacheKey, artists);
        return artists;
    }

    public Artist getArtistById(long id) {
        Artist artist = artistRepository.findArtistById(id);
        String cacheKey = "artists_id_" + id;
        if (cache.containsKey(cacheKey)) {
            return (Artist) cache.get(cacheKey);
        }
        cache.put(cacheKey, artist);
        return artist;
    }

    public Artist getArtistByName(String name)  {
        Artist artist = artistRepository.findArtistByName(name);
        String cacheKey = "artists_name_" + name;
        if (cache.containsKey(cacheKey)) {
            return (Artist) cache.get(cacheKey);
        }
        cache.put(cacheKey, artist);
        return artist;
    }

    public Artist createArtist(ArtistCreateDto artistDto) {
        Artist artist = new Artist();
        artist.setName(artistDto.getName());
        if (artistDto.getAlbumsIds() != null) {
            List<Album> albums = albumRepository.findAllById(artistDto.getAlbumsIds());
            artist.setAlbums(albums);
        }
        cache.clear();
        return artistRepository.save(artist);
    }

    public Artist updateArtist(long id, ArtistUpdateDto artistDto) {
        Artist artist = getArtistById(id);
        if (artist == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Artist not found");
        }
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
                                new ResponseStatusException(HttpStatus.NOT_FOUND,
                                     "Artist not found"))).toList();
            artist.setAlbums(albums);
        }
        cache.clear();
        return artistRepository.save(artist);

    }

    public void deleteArtist(Long id) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Artist was not found"));
        artistRepository.delete(artist);
        cache.clear();
    }

}
