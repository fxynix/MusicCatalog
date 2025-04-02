package musiccatalog.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import musiccatalog.dto.create.ArtistCreateDto;
import musiccatalog.dto.update.ArtistUpdateDto;
import musiccatalog.exception.NotFoundException;
import musiccatalog.model.Album;
import musiccatalog.model.Artist;
import musiccatalog.repository.AlbumRepository;
import musiccatalog.repository.ArtistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ArtistServiceTest {

    @Mock
    private ArtistRepository artistRepository;

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private InMemoryCache cache;

    @InjectMocks
    private ArtistService artistService;

    private Artist testArtist;
    private Album testAlbum;

    @BeforeEach
    void setUp() {
        testArtist = new Artist();
        testArtist.setId(1L);
        testArtist.setName("Test Artist");

        testAlbum = new Album();
        testAlbum.setId(1L);
        testAlbum.setName("Test Album");
    }

    @Test
    void getAllArtists_ShouldReturnAllArtists() {
        when(artistRepository.findAll()).thenReturn(List.of(testArtist));
        List<Artist> result = artistService.getAllArtists();
        assertEquals(1, result.size());
        verify(artistRepository).findAll();
    }

    @Test
    void getArtistById_WhenCached_ShouldReturnFromCache() {
        String cacheKey = "artists_id_1";
        when(cache.containsKey(cacheKey)).thenReturn(true);
        when(cache.get(cacheKey)).thenReturn(testArtist);
        Optional<Artist> result = artistService.getArtistById(1L);
        assertTrue(result.isPresent());
        assertEquals(testArtist, result.get());
        verify(artistRepository, never()).findById(anyLong());
    }

    @Test
    void getArtistById_WhenNotCached_ShouldFetchFromRepository() {
        String cacheKey = "artists_id_1";
        when(cache.containsKey(cacheKey)).thenReturn(false);
        when(artistRepository.findById(1L)).thenReturn(Optional.of(testArtist));
        Optional<Artist> result = artistService.getArtistById(1L);
        assertTrue(result.isPresent());
        assertEquals(testArtist, result.get());
        verify(cache).put(cacheKey, testArtist);
        verify(artistRepository).findById(1L);
    }

    @Test
    void getArtistById_WhenNotFound_ShouldThrowException() {
        when(artistRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> artistService.getArtistById(1L));
    }

    @Test
    void getArtistByName_WhenCached_ShouldReturnFromCache() {
        String cacheKey = "artists_name_Test Artist";
        when(cache.containsKey(cacheKey)).thenReturn(true);
        when(cache.get(cacheKey)).thenReturn(testArtist);
        Artist result = artistService.getArtistByName("Test Artist");
        assertEquals(testArtist, result);
        verify(artistRepository, never()).findArtistByName(anyString());
    }

    @Test
    void getArtistByName_WhenNotCached_ShouldFetchFromRepository() {
        String cacheKey = "artists_name_Test Artist";
        when(cache.containsKey(cacheKey)).thenReturn(false);
        when(artistRepository.findArtistByName("Test Artist")).thenReturn(testArtist);
        Artist result = artistService.getArtistByName("Test Artist");
        assertEquals(testArtist, result);
        verify(cache).put(cacheKey, testArtist);
        verify(artistRepository).findArtistByName("Test Artist");
    }

    @Test
    void createArtist_ShouldSaveAndClearCache() {
        ArtistCreateDto dto = new ArtistCreateDto();
        dto.setName("New Artist");
        when(artistRepository.save(any(Artist.class))).thenAnswer(invocation -> {
            Artist artist = invocation.getArgument(0);
            artist.setId(2L);
            return artist;
        });
        Artist result = artistService.createArtist(dto);
        assertEquals("New Artist", result.getName());
        verify(artistRepository).save(any(Artist.class));
        verify(cache).clear();
    }

    @Test
    void updateArtist_ShouldUpdateNameAndAlbums() {
        ArtistUpdateDto dto = new ArtistUpdateDto();
        dto.setName("Updated Name");
        dto.setAlbumsIds(List.of(1L));

        when(artistRepository.findById(1L)).thenReturn(Optional.of(testArtist));
        when(albumRepository.findById(1L)).thenReturn(Optional.of(testAlbum));
        when(artistRepository.save(any(Artist.class))).thenReturn(testArtist);

        Artist result = artistService.updateArtist(1L, dto);

        assertNotNull(result);
        assertEquals("Updated Name", result.getName());
        assertEquals(1, result.getAlbums().size());
        verify(artistRepository).save(testArtist);
        verify(cache).clear();
    }

    @Test
    void updateArtist_WhenAlbumNotFound_ShouldThrowException() {
        ArtistUpdateDto dto = new ArtistUpdateDto();
        dto.setAlbumsIds(List.of(99L));
        when(artistRepository.findById(1L)).thenReturn(Optional.of(testArtist));
        when(albumRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> artistService.updateArtist(1L, dto));
    }

    @Test
    void deleteArtist_ShouldDeleteAndClearCache() {
        when(artistRepository.findById(1L)).thenReturn(Optional.of(testArtist));
        artistService.deleteArtist(1L);
        verify(artistRepository).delete(testArtist);
        verify(cache).clear();
    }

    @Test
    void deleteArtist_WhenNotFound_ShouldThrowException() {
        when(artistRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> artistService.deleteArtist(1L));
    }
}