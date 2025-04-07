package musiccatalog.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import musiccatalog.cache.InMemoryCache;
import musiccatalog.dto.create.PlaylistCreateDto;
import musiccatalog.dto.update.PlaylistUpdateDto;
import musiccatalog.exception.NotFoundException;
import musiccatalog.model.Playlist;
import musiccatalog.model.Track;
import musiccatalog.model.User;
import musiccatalog.repository.PlaylistRepository;
import musiccatalog.repository.TrackRepository;
import musiccatalog.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PlaylistServiceTest {

    @Mock
    private PlaylistRepository playlistRepository;

    @Mock
    private TrackRepository trackRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private InMemoryCache cache;

    @InjectMocks
    private PlaylistService playlistService;

    private Playlist testPlaylist;
    private Track testTrack;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("testUser");

        testTrack = new Track();
        testTrack.setId(1L);
        testTrack.setName("Test Track");
        testTrack.setPlaylists(new ArrayList<>());

        testPlaylist = new Playlist();
        testPlaylist.setId(1L);
        testPlaylist.setName("Test Playlist");
        testPlaylist.setAuthor(testUser);
        testPlaylist.setTracks(List.of(testTrack));

    }

    @Test
    void getAllPlaylists_ShouldReturnAllPlaylists() {
        when(playlistRepository.findAll()).thenReturn(List.of(testPlaylist));

        List<Playlist> result = playlistService.getAllPlaylists();

        assertEquals(1, result.size());
        verify(playlistRepository).findAll();
    }

    @Test
    void getPlaylistById_WhenCached_ShouldReturnFromCache() {
        String cacheKey = "playlists_id_1";
        when(cache.containsKey(cacheKey)).thenReturn(true);
        when(cache.get(cacheKey)).thenReturn(testPlaylist);

        Optional<Playlist> result = playlistService.getPlaylistById(1L);

        assertTrue(result.isPresent());
        assertEquals(testPlaylist, result.get());
        verify(playlistRepository, never()).findById(anyLong());
    }

    @Test
    void getPlaylistById_WhenNotCached_ShouldFetchFromRepository() {
        String cacheKey = "playlists_id_1";
        when(cache.containsKey(cacheKey)).thenReturn(false);
        when(playlistRepository.findById(1L)).thenReturn(Optional.of(testPlaylist));

        Optional<Playlist> result = playlistService.getPlaylistById(1L);

        assertTrue(result.isPresent());
        assertEquals(testPlaylist, result.get());
        verify(cache).put(cacheKey, testPlaylist);
    }

    @Test
    void getPlaylistByName_WhenCached_ShouldReturnFromCache() {
        String cacheKey = "playlists_name_Test Playlist";
        when(cache.containsKey(cacheKey)).thenReturn(true);
        when(cache.get(cacheKey)).thenReturn(List.of(testPlaylist));

        List<Playlist> result = playlistService.getPlaylistByName("Test Playlist");

        assertEquals(1, result.size());
        verify(playlistRepository, never()).findPlaylistByName(anyString());
    }

    @Test
    void createPlaylist_ShouldSaveWithCorrectData() {
        PlaylistCreateDto dto = new PlaylistCreateDto();
        dto.setName("New Playlist");
        dto.setAuthorId(1L);
        dto.setTracksIds(List.of(1L));

        when(trackRepository.findAllById(anyList())).thenReturn(List.of(testTrack));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(playlistRepository.save(any(Playlist.class))).thenAnswer(invocation -> {
            Playlist saved = invocation.getArgument(0);
            saved.setId(1L); // Добавляем установку ID
            return saved;
        });

        Playlist result = playlistService.createPlaylist(dto);

        assertEquals("New Playlist", result.getName());
        assertEquals(testUser, result.getAuthor());
        assertEquals(1, result.getTracks().size());
        verify(cache).clear();
    }

    @Test
    void updatePlaylist_ShouldUpdateAllFields() {
        PlaylistUpdateDto dto = new PlaylistUpdateDto();
        dto.setName("Updated Playlist");
        dto.setAuthorId(2L);
        dto.setTracksIds(List.of(2L));

        User newUser = new User();
        newUser.setId(2L);
        Track newTrack = new Track();
        newTrack.setId(2L);
        newTrack.setPlaylists(new ArrayList<>());

        when(playlistRepository.findById(1L)).thenReturn(Optional.of(testPlaylist));
        when(userRepository.findById(2L)).thenReturn(Optional.of(newUser));
        when(trackRepository.findById(2L)).thenReturn(Optional.of(newTrack));
        when(playlistRepository.save(any(Playlist.class))).thenReturn(testPlaylist);

        Playlist result = playlistService.updatePlaylist(1L, dto);

        assertNotNull(result);
        assertEquals("Updated Playlist", result.getName());
        assertEquals(newUser, result.getAuthor());
        assertEquals(1, result.getTracks().size());
        assertTrue(newTrack.getPlaylists().contains(testPlaylist));
        verify(cache).clear();
    }

    @Test
    void deletePlaylist_ShouldDeleteAndClearCache() {
        when(playlistRepository.findById(1L)).thenReturn(Optional.of(testPlaylist));

        playlistService.deletePlaylist(1L);

        verify(playlistRepository).delete(testPlaylist);
        verify(cache).clear();
    }

    @Test
    void createPlaylist_WhenAuthorNotFound_ShouldThrowException() {
        PlaylistCreateDto dto = new PlaylistCreateDto();
        dto.setName("New Playlist");
        dto.setTracksIds(List.of());
        dto.setAuthorId(99L);

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> playlistService.createPlaylist(dto));
    }

    @Test
    void updatePlaylist_WhenTrackNotFound_ShouldThrowException() {
        PlaylistUpdateDto dto = new PlaylistUpdateDto();
        dto.setTracksIds(List.of(99L));

        when(playlistRepository.findById(1L)).thenReturn(Optional.of(testPlaylist));
        when(trackRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> playlistService.updatePlaylist(1L, dto));
    }

    @Test
    void getPlaylistByName_WhenNotCached_ShouldFetchFromRepository() {
        String cacheKey = "playlists_name_Test";
        when(cache.containsKey(cacheKey)).thenReturn(false);
        when(playlistRepository.findPlaylistByName("Test")).thenReturn(List.of(testPlaylist));

        List<Playlist> result = playlistService.getPlaylistByName("Test");

        assertEquals(1, result.size());
        verify(cache).put(cacheKey, result);
    }

    @Test
    void getPlaylistById_WhenNotFound_ShouldThrowException() {
        when(playlistRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> playlistService.getPlaylistById(99L));
    }

    @Test
    void updatePlaylist_ShouldUpdateOnlyName() {
        PlaylistUpdateDto dto = new PlaylistUpdateDto();
        dto.setName("New Name Only");

        when(playlistRepository.findById(1L)).thenReturn(Optional.of(testPlaylist));
        when(playlistRepository.save(any(Playlist.class))).thenReturn(testPlaylist);

        Playlist result = playlistService.updatePlaylist(1L, dto);

        assertNotNull(result);
        assertEquals("New Name Only", result.getName());
        assertEquals(testUser, result.getAuthor());
        assertEquals(testPlaylist.getTracks(), result.getTracks());
        verify(cache).clear();
    }
}