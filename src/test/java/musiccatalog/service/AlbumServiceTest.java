package musiccatalog.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import musiccatalog.dto.create.AlbumCreateDto;
import musiccatalog.dto.update.AlbumUpdateDto;
import musiccatalog.exception.NotFoundException;
import musiccatalog.model.Album;
import musiccatalog.model.Artist;
import musiccatalog.model.Track;
import musiccatalog.repository.AlbumRepository;
import musiccatalog.repository.ArtistRepository;
import musiccatalog.repository.TrackRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AlbumServiceTest {

    @Mock
    private AlbumRepository albumRepository;
    @Mock
    private ArtistRepository artistRepository;
    @Mock
    private TrackRepository trackRepository;
    @Mock
    private InMemoryCache cache;

    @InjectMocks
    private AlbumService albumService;

    private Album testAlbum;
    private Artist testArtist;

    @BeforeEach
    void setUp() {

        testArtist = new Artist();
        testArtist.setId(1L);
        testArtist.setName("Test Artist");
        testArtist.setAlbums(new ArrayList<>());

        Track testTrack;
        testTrack = new Track();
        testTrack.setId(1L);
        testTrack.setName("Test Track");

        testAlbum = new Album();
        testAlbum.setId(1L);
        testAlbum.setName("Test Album");
        testAlbum.setArtists(new ArrayList<>(List.of(testArtist)));
        testAlbum.setTracks(new ArrayList<>(List.of(testTrack)));
    }

    // region Create Album Tests
    @Test
    void createAlbum_WithValidData_ShouldSaveSuccessfully() {
        AlbumCreateDto dto = new AlbumCreateDto();
        dto.setName("New Album");
        dto.setArtistsIds(List.of(1L));

        when(artistRepository.findAllById(anyList())).thenReturn(List.of(testArtist));
        when(albumRepository.save(any())).thenAnswer(inv -> {
            Album a = inv.getArgument(0);
            a.setId(2L);
            return a;
        });

        Album result = albumService.createAlbum(dto);

        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals(1, result.getArtists().size());
        verify(cache).clear();
    }

    @Test
    void createAlbum_WithNonExistingArtists_ShouldThrowException() {
        AlbumCreateDto dto = new AlbumCreateDto();
        dto.setArtistsIds(List.of(99L));

        when(artistRepository.findAllById(anyList())).thenReturn(Collections.emptyList());

        assertThrows(NotFoundException.class,
                () -> albumService.createAlbum(dto));
        verify(albumRepository, never()).save(any());
    }

    @Test
    void createAlbum_WithPartialExistingArtists_ShouldThrowException() {
        AlbumCreateDto dto = new AlbumCreateDto();
        dto.setArtistsIds(List.of(1L, 99L));

        when(artistRepository.findAllById(anyList())).thenReturn(List.of(testArtist));

        assertThrows(NotFoundException.class,
                () -> albumService.createAlbum(dto));
        verify(albumRepository, never()).save(any());
    }

    @Test
    void updateAlbum_ShouldUpdateAllFields() {
        Artist newArtist = new Artist();
        newArtist.setId(2L);
        newArtist.setAlbums(new ArrayList<>());

        Track newTrack = new Track();
        newTrack.setId(2L);

        AlbumUpdateDto dto = new AlbumUpdateDto();
        dto.setName("Updated Album");
        dto.setArtistsIds(List.of(2L));
        dto.setTracksIds(List.of(2L));

        when(albumRepository.findById(1L)).thenReturn(Optional.of(testAlbum));
        when(artistRepository.findById(2L)).thenReturn(Optional.of(newArtist));
        when(trackRepository.findAllById(anyList())).thenReturn(List.of(newTrack));
        when(albumRepository.save(any(Album.class))).thenReturn(testAlbum);

        Album result = albumService.updateAlbum(1L, dto);

        assertNotNull(result);
        assertTrue(newArtist.getAlbums().contains(testAlbum));
        assertFalse(testArtist.getAlbums().contains(testAlbum));
    }

    @Test
    void updateAlbum_WithInvalidArtists_ShouldThrowException() {
        AlbumUpdateDto dto = new AlbumUpdateDto();
        dto.setArtistsIds(List.of(99L));

        when(albumRepository.findById(1L)).thenReturn(Optional.of(testAlbum));
        when(artistRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> albumService.updateAlbum(1L, dto));
    }

    @Test
    void updateAlbum_ShouldUpdateOnlyName() {
        AlbumUpdateDto dto = new AlbumUpdateDto();
        dto.setName("New Name Only");

        when(albumRepository.findById(1L)).thenReturn(Optional.of(testAlbum));
        when(albumRepository.save(any())).thenReturn(testAlbum);

        Album result = albumService.updateAlbum(1L, dto);

        assertEquals("New Name Only", result.getName());
        assertEquals(1, result.getArtists().size());
        verify(cache).clear();
    }
    // endregion

    // region Get Album Tests
    @Test
    void getAlbumById_WhenCached_ShouldReturnFromCache() {
        String cacheKey = "albums_id_1";
        when(cache.containsKey(cacheKey)).thenReturn(true);
        when(cache.get(cacheKey)).thenReturn(testAlbum);

        Optional<Album> result = albumService.getAlbumById(1L);

        assertTrue(result.isPresent());
        assertEquals(testAlbum, result.get());
        verify(albumRepository, never()).findById(any());
    }

    @Test
    void getAlbumById_WhenNotCached_ShouldFetchFromDB() {
        String cacheKey = "albums_id_1";
        when(cache.containsKey(cacheKey)).thenReturn(false);
        when(albumRepository.findById(1L)).thenReturn(Optional.of(testAlbum));

        Optional<Album> result = albumService.getAlbumById(1L);

        assertTrue(result.isPresent());
        verify(cache).put(cacheKey, testAlbum);
    }

    @Test
    void getAlbumById_WhenNotFound_ShouldThrowException() {
        when(albumRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> albumService.getAlbumById(99L));
    }

    @Test
    void getAlbumByName_WhenCached_ShouldReturnFromCache() {
        String cacheKey = "albums_name_Test";
        when(cache.containsKey(cacheKey)).thenReturn(true);
        when(cache.get(cacheKey)).thenReturn(List.of(testAlbum));

        List<Album> result = albumService.getAlbumByName("Test");

        assertEquals(1, result.size());
        verify(albumRepository, never()).findAlbumsByName(any());
    }

    @Test
    void getAlbumsByGenreName_WhenEmpty_ShouldThrowException() {
        String genre = "Unknown";
        when(albumRepository.findAlbumsByGenreName(genre)).thenReturn(Collections.emptyList());

        assertThrows(NotFoundException.class,
                () -> albumService.getAlbumsByGenreName(genre));
    }
    // endregion

    // region Delete Album Tests
    @Test
    void deleteAlbum_ShouldRemoveFromDatabase() {
        when(albumRepository.findById(1L)).thenReturn(Optional.of(testAlbum));

        albumService.deleteAlbum(1L);

        verify(albumRepository).delete(testAlbum);
        verify(cache).clear();
    }

    @Test
    void deleteAlbum_WhenNotFound_ShouldThrowException() {
        when(albumRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> albumService.deleteAlbum(99L));
    }
    // endregion

    // region Relationships Tests
    @Test
    void updateAlbum_ShouldMaintainBidirectionalRelationship() {
        Artist newArtist = new Artist();
        newArtist.setId(2L);
        newArtist.setAlbums(new ArrayList<>());

        AlbumUpdateDto dto = new AlbumUpdateDto();
        dto.setArtistsIds(List.of(2L));

        when(albumRepository.findById(1L)).thenReturn(Optional.of(testAlbum));
        when(artistRepository.findById(2L)).thenReturn(Optional.of(newArtist));
        when(albumRepository.save(any())).thenReturn(testAlbum);

        albumService.updateAlbum(1L, dto);

        assertTrue(newArtist.getAlbums().contains(testAlbum));
        assertFalse(testArtist.getAlbums().contains(testAlbum));
    }
    // endregion
}