package musiccatalog.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import musiccatalog.cache.InMemoryCache;
import musiccatalog.dto.create.UserCreateDto;
import musiccatalog.dto.update.UserUpdateDto;
import musiccatalog.exception.ConflictException;
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
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PlaylistRepository playlistRepository;

    @Mock
    private TrackRepository trackRepository;

    @Mock
    private InMemoryCache cache;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private Playlist testPlaylist;
    private Track testTrack;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");

        testPlaylist = new Playlist();
        testPlaylist.setId(1L);
        testPlaylist.setSubscribers(new ArrayList<>());

        testTrack = new Track();
        testTrack.setId(1L);
        testTrack.setLikedByUsers(new ArrayList<>());
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(testUser));

        List<User> result = userService.getAllUsers();

        assertEquals(1, result.size());
        verify(userRepository).findAll();
    }

    @Test
    void getUserById_WhenCached_ShouldReturnFromCache() {
        String cacheKey = "users_id_1";
        when(cache.containsKey(cacheKey)).thenReturn(true);
        when(cache.get(cacheKey)).thenReturn(testUser);

        Optional<User> result = userService.getUserById(1L);

        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());
        verify(userRepository, never()).findById(anyLong());
    }

    @Test
    void createUser_ShouldSaveUserAndClearCache() {
        UserCreateDto dto = new UserCreateDto();
        dto.setName("New User");
        dto.setEmail("new@example.com");
        dto.setPassword("newpass");

        when(userRepository.findUserByEmail("new@example.com")).thenReturn(null);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setId(2L);
            return saved;
        });

        User result = userService.createUser(dto);

        assertNotNull(result);
        assertEquals("New User", result.getName());
        assertEquals("new@example.com", result.getEmail());
        verify(cache).clear();
    }

    @Test
    void createUser_WhenEmailExists_ShouldThrowConflictException() {
        UserCreateDto dto = new UserCreateDto();
        dto.setEmail("existing@example.com");

        when(userRepository.findUserByEmail("existing@example.com"))
                .thenReturn(new User());

        assertThrows(ConflictException.class, () -> userService.createUser(dto));
    }

    @Test
    void updateUser_ShouldUpdateAllFields() {
        UserUpdateDto dto = new UserUpdateDto();
        dto.setName("Updated User");
        dto.setEmail("updated@example.com");
        dto.setPassword("newpassword");
        dto.setSubscribedPlaylistsIds(List.of(1L));
        dto.setLikedTracksIds(List.of(1L));

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findUserByEmail("updated@example.com")).thenReturn(null);
        when(playlistRepository.findById(1L)).thenReturn(Optional.of(testPlaylist));
        when(trackRepository.findById(1L)).thenReturn(Optional.of(testTrack));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.updateUser(1L, dto);

        assertNotNull(result);
        assertEquals("Updated User", result.getName());
        assertEquals("updated@example.com", result.getEmail());
        assertEquals(1, result.getPlaylistsSubscribed().size());
        assertEquals(1, result.getLikedTracks().size());
        assertTrue(testPlaylist.getSubscribers().contains(testUser));
        assertTrue(testTrack.getLikedByUsers().contains(testUser));
        verify(cache).clear();
    }

    @Test
    void updateUser_WhenEmailTaken_ShouldThrowConflictException() {
        UserUpdateDto dto = new UserUpdateDto();
        dto.setEmail("taken@example.com");

        User otherUser = new User();
        otherUser.setId(2L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findUserByEmail("taken@example.com")).thenReturn(otherUser);

        assertThrows(ConflictException.class, () -> userService.updateUser(1L, dto));
    }

    @Test
    void updateUser_WhenPlaylistNotFound_ShouldThrowNotFoundException() {
        UserUpdateDto dto = new UserUpdateDto();
        dto.setSubscribedPlaylistsIds(List.of(99L));

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(playlistRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.updateUser(1L, dto));
    }

    @Test
    void deleteUser_ShouldDeleteAndClearCache() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        userService.deleteUser(1L);

        verify(userRepository).delete(testUser);
        verify(cache).clear();
    }

    @Test
    void getUserByName_ShouldReturnCachedUser() {
        String cacheKey = "users_name_Test User";
        when(cache.containsKey(cacheKey)).thenReturn(true);
        when(cache.get(cacheKey)).thenReturn(testUser);

        User result = userService.getUserByName("Test User");

        assertEquals(testUser, result);
        verify(userRepository, never()).findUserByName(anyString());
    }

    @Test
    void getUserById_WhenNotCached_ShouldFetchFromRepository() {
        String cacheKey = "users_id_1";
        when(cache.containsKey(cacheKey)).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.getUserById(1L);

        assertTrue(result.isPresent());
        verify(cache).put(cacheKey, testUser);
    }

    @Test
    void getUserById_WhenNotFound_ShouldThrowException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.getUserById(99L));
    }

    @Test
    void getUserByName_WhenNotCached_ShouldFetchFromRepository() {
        String cacheKey = "users_name_Test User";
        when(cache.containsKey(cacheKey)).thenReturn(false);
        when(userRepository.findUserByName("Test User")).thenReturn(testUser);

        User result = userService.getUserByName("Test User");

        assertEquals(testUser, result);
        verify(cache).put(cacheKey, testUser);
    }

    @Test
    void updateUser_ShouldUpdateOnlyName() {
        UserUpdateDto dto = new UserUpdateDto();
        dto.setName("New Name Only");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.updateUser(1L, dto);

        assertEquals("New Name Only", result.getName());
        assertEquals(testUser.getEmail(), result.getEmail());
        assertEquals(testUser.getPassword(), result.getPassword());
        verify(cache).clear();
    }

}