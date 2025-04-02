package musiccatalog.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import musiccatalog.dto.create.TrackCreateDto;
import musiccatalog.dto.update.TrackUpdateDto;
import musiccatalog.exception.NotFoundException;
import musiccatalog.model.Album;
import musiccatalog.model.Genre;
import musiccatalog.model.Track;
import musiccatalog.repository.AlbumRepository;
import musiccatalog.repository.GenreRepository;
import musiccatalog.repository.TrackRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TrackServiceTest {

    @Mock
    private TrackRepository trackRepository;

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private GenreRepository genreRepository;

    @Mock
    private InMemoryCache cache;

    @InjectMocks
    private TrackService trackService;

    private Track testTrack;
    private Album testAlbum;
    private Genre testGenre;

    @BeforeEach
    void setUp() {
        testAlbum = new Album();
        testAlbum.setId(1L);
        testAlbum.setName("Test Album");

        testGenre = new Genre();
        testGenre.setId(1L);
        testGenre.setName("Test Genre");
        testGenre.setTracks(new ArrayList<>());

        testTrack = new Track();
        testTrack.setId(1L);
        testTrack.setName("Test Track");
        testTrack.setDuration(180);
        testTrack.setAlbum(testAlbum);
        testTrack.setGenres(Collections.singletonList(testGenre));
    }

    // Existing tests...

    @Test
    void getTrackById_WhenNotCached_ShouldFetchFromRepository() {
        String cacheKey = "tracks_id_1";
        when(cache.containsKey(cacheKey)).thenReturn(false);
        when(trackRepository.findById(1L)).thenReturn(Optional.of(testTrack));

        Optional<Track> result = trackService.getTrackById(1L);

        assertTrue(result.isPresent());
        verify(cache).put(cacheKey, testTrack);
    }

    @Test
    void getTrackById_WhenNotFound_ShouldThrowException() {
        when(trackRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> trackService.getTrackById(1L));
    }

    @Test
    void getTrackByName_WhenNotCached_ShouldFetchFromRepository() {
        String cacheKey = "tracks_name_Test";
        when(cache.containsKey(cacheKey)).thenReturn(false);
        when(trackRepository.findTracksByName("Test")).thenReturn(List.of(testTrack));

        List<Track> result = trackService.getTrackByName("Test");

        assertEquals(1, result.size());
        verify(cache).put(cacheKey, result);
    }

    @Test
    void getTracksByArtistName_WhenNotCached_ShouldFetchFromRepository() {
        String cacheKey = "tracks_artist_TestArtist";
        when(cache.containsKey(cacheKey)).thenReturn(false);
        when(trackRepository.findTracksByArtistName("TestArtist")).thenReturn(List.of(testTrack));

        List<Track> result = trackService.getTracksByArtistName("TestArtist");

        assertEquals(1, result.size());
        verify(cache).put(cacheKey, result);
    }

    @Test
    void updateTrack_ShouldUpdateOnlyName() {
        TrackUpdateDto dto = new TrackUpdateDto();
        dto.setName("New Name");

        when(trackRepository.findById(1L)).thenReturn(Optional.of(testTrack));
        when(trackRepository.save(any(Track.class))).thenReturn(testTrack);

        Track result = trackService.updateTrack(1L, dto);

        assertEquals("New Name", result.getName());
        assertEquals(180, result.getDuration()); // Original duration remains
        verify(cache).clear();
    }

    @Test
    void updateTrack_ShouldUpdateOnlyAlbum() {
        Album newAlbum = new Album();
        newAlbum.setId(2L);

        TrackUpdateDto dto = new TrackUpdateDto();
        dto.setAlbumId(2L);

        when(trackRepository.findById(1L)).thenReturn(Optional.of(testTrack));
        when(albumRepository.findById(2L)).thenReturn(Optional.of(newAlbum));
        when(trackRepository.save(any(Track.class))).thenReturn(testTrack);

        Track result = trackService.updateTrack(1L, dto);

        assertNotNull(result);
        assertEquals(newAlbum, result.getAlbum());
        verify(cache).clear();
    }

    @Test
    void updateTrack_WithEmptyGenres_ShouldClearGenres() {
        TrackUpdateDto dto = new TrackUpdateDto();
        dto.setGenresIds(Collections.emptyList());

        when(trackRepository.findById(1L)).thenReturn(Optional.of(testTrack));
        when(genreRepository.findAllById(anyList())).thenReturn(Collections.emptyList());
        when(trackRepository.save(any(Track.class))).thenAnswer(invocation ->
                invocation.getArgument(0)
        );

        Track result = trackService.updateTrack(1L, dto);

        assertNotNull(result);
        assertTrue(result.getGenres().isEmpty());
        verify(cache).clear();
    }

    @Test
    void updateTrack_WithNullGenres_ShouldNotModifyGenres() {
        TrackUpdateDto dto = new TrackUpdateDto();
        dto.setGenresIds(null);

        when(trackRepository.findById(1L)).thenReturn(Optional.of(testTrack));
        when(trackRepository.save(any(Track.class))).thenAnswer(invocation ->
                invocation.getArgument(0)
        );

        Track result = trackService.updateTrack(1L, dto);

        assertNotNull(result);
        assertEquals(1, result.getGenres().size());
        verify(cache).clear();
    }

    @Test
    void updateTrack_ShouldAddTrackToGenreCollection() {
        TrackUpdateDto dto = new TrackUpdateDto();
        dto.setGenresIds(List.of(1L));

        when(trackRepository.findById(1L)).thenReturn(Optional.of(testTrack));
        when(genreRepository.findById(1L)).thenReturn(Optional.of(testGenre));

        trackService.updateTrack(1L, dto);

        assertTrue(testGenre.getTracks().contains(testTrack));
    }

    @Test
    void updateTrack_WithZeroDuration_ShouldNotUpdate() {
        TrackUpdateDto dto = new TrackUpdateDto();
        dto.setDuration(0);

        when(trackRepository.findById(1L)).thenReturn(Optional.of(testTrack));
        when(trackRepository.save(any(Track.class))).thenAnswer(invocation ->
                invocation.getArgument(0)
        );

        Track result = trackService.updateTrack(1L, dto);

        assertNotNull(result); // Проверка на null
        assertEquals(180, result.getDuration());
    }

    @Test
    void getTrackByName_WhenNotFound_ShouldReturnEmptyList() {
        String cacheKey = "tracks_name_Unknown";
        when(cache.containsKey(cacheKey)).thenReturn(false);
        when(trackRepository.findTracksByName("Unknown")).thenReturn(Collections.emptyList());

        List<Track> result = trackService.getTrackByName("Unknown");

        assertTrue(result.isEmpty());
        verify(cache).put(cacheKey, result);
    }

    @Test
    void createTrack_WithEmptyGenres_ShouldSaveSuccessfully() {
        TrackCreateDto dto = new TrackCreateDto();
        dto.setName("Track");
        dto.setAlbumId(1L);
        dto.setGenresIds(Collections.emptyList());

        when(albumRepository.findById(1L)).thenReturn(Optional.of(testAlbum));
        when(genreRepository.findAllById(anyList())).thenReturn(Collections.emptyList());
        when(trackRepository.save(any(Track.class))).thenAnswer(invocation -> {
            Track saved = invocation.getArgument(0);
            saved.setId(1L); // Добавляем установку ID
            return saved;
        });

        Track result = trackService.createTrack(dto);

        assertNotNull(result); // Проверяем что объект не null
        assertTrue(result.getGenres().isEmpty());
        verify(cache).clear();
    }
}