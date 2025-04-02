package musiccatalog.service;

import musiccatalog.dto.create.GenreCreateDto;
import musiccatalog.dto.update.GenreUpdateDto;
import musiccatalog.exception.ConflictException;
import musiccatalog.exception.NotFoundException;
import musiccatalog.model.Genre;
import musiccatalog.repository.GenreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenreServiceTest {

    @Mock
    private GenreRepository genreRepository;

    @Mock
    private InMemoryCache cache;

    @InjectMocks
    private GenreService genreService;

    private Genre testGenre;
    private final Long genreId = 1L;

    @BeforeEach
    void setUp() {
        testGenre = new Genre();
        testGenre.setId(genreId);
        testGenre.setName("Rock");
    }

    @Test
    void getAllGenres_ShouldReturnAllGenres() {
        when(genreRepository.findAll()).thenReturn(List.of(testGenre));

        List<Genre> result = genreService.getAllGenres();

        assertEquals(1, result.size());
        verify(genreRepository).findAll();
    }

    @Test
    void getGenreById_WhenCached_ShouldReturnFromCache() {
        String cacheKey = "genres_id_" + genreId;
        when(cache.containsKey(cacheKey)).thenReturn(true);
        when(cache.get(cacheKey)).thenReturn(testGenre);

        Optional<Genre> result = genreService.getGenreById(genreId);

        assertTrue(result.isPresent());
        assertEquals(testGenre, result.get());
        verify(cache).containsKey(cacheKey);
        verifyNoInteractions(genreRepository);
    }

    @Test
    void getGenreById_WhenNotCached_ShouldFetchFromRepository() {
        String cacheKey = "genres_id_" + genreId;
        when(genreRepository.findById(genreId)).thenReturn(Optional.of(testGenre));

        Optional<Genre> result = genreService.getGenreById(genreId);

        assertTrue(result.isPresent());
        assertEquals(testGenre, result.get());
        verify(cache).put(cacheKey, testGenre);
        verify(genreRepository).findById(genreId);
    }

    @Test
    void getGenreById_WhenNotFound_ShouldThrowException() {
        when(genreRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> genreService.getGenreById(999L));
    }

    @Test
    void getGenreByName_WhenCached_ShouldReturnFromCache() {
        String name = "Rock";
        String cacheKey = "genres_name_" + name;
        when(cache.containsKey(cacheKey)).thenReturn(true);
        when(cache.get(cacheKey)).thenReturn(testGenre);

        Genre result = genreService.getGenreByName(name);

        assertEquals(testGenre, result);
        verifyNoInteractions(genreRepository);
    }

    @Test
    void getGenreByName_WhenNotCached_ShouldFetchFromRepository() {
        String name = "Rock";
        when(genreRepository.findGenreByName(name)).thenReturn(testGenre);

        Genre result = genreService.getGenreByName(name);

        assertEquals(testGenre, result);
        verify(cache).put("genres_name_" + name, testGenre);
    }

    @Test
    void createGenre_ShouldSaveNewGenreAndClearCache() {
        GenreCreateDto dto = new GenreCreateDto();
        dto.setName("Jazz");
        when(genreRepository.findGenreByName(anyString())).thenReturn(null);
        when(genreRepository.save(any(Genre.class))).thenReturn(testGenre);

        Genre result = genreService.createGenre(dto);

        assertNotNull(result);
        verify(genreRepository).save(any(Genre.class));
        verify(cache).clear();
    }

    @Test
    void createGenre_WhenNameExists_ShouldThrowConflictException() {
        GenreCreateDto dto = new GenreCreateDto();
        dto.setName("Rock");
        when(genreRepository.findGenreByName(anyString())).thenReturn(testGenre);

        assertThrows(ConflictException.class, () -> genreService.createGenre(dto));
    }

    @Test
    void updateGenre_ShouldUpdateExistingGenre() {
        GenreUpdateDto dto = new GenreUpdateDto();
        dto.setName("Progressive Rock");

        when(genreRepository.findById(genreId)).thenReturn(Optional.of(testGenre));
        when(genreRepository.findGenreByName(anyString())).thenReturn(null);
        when(genreRepository.save(any(Genre.class))).thenReturn(testGenre);

        Genre result = genreService.updateGenre(genreId, dto);

        assertNotNull(result);
        assertEquals("Progressive Rock", result.getName());
        verify(genreRepository).save(testGenre);
        verify(cache).clear();
    }

    @Test
    void updateGenre_WhenNameConflict_ShouldThrowConflictException() {
        Genre existingGenre = new Genre();
        existingGenre.setId(2L);
        existingGenre.setName("Pop");

        GenreUpdateDto dto = new GenreUpdateDto();
        dto.setName("Pop");

        when(genreRepository.findById(genreId)).thenReturn(Optional.of(testGenre));
        when(genreRepository.findGenreByName("Pop")).thenReturn(existingGenre);

        assertThrows(ConflictException.class, () -> genreService.updateGenre(genreId, dto));
    }

    @Test
    void deleteGenre_ShouldRemoveGenreAndClearCache() {
        when(genreRepository.findById(genreId)).thenReturn(Optional.of(testGenre));

        genreService.deleteGenre(genreId);

        verify(genreRepository).delete(testGenre);
        verify(cache).clear();
    }

    @Test
    void deleteGenre_WhenNotFound_ShouldThrowException() {
        when(genreRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> genreService.deleteGenre(999L));
    }

    @Test
    void updateGenre_ShouldAllowSameNameForSameId() {
        GenreUpdateDto dto = new GenreUpdateDto();
        dto.setName("Rock");

        when(genreRepository.findById(genreId)).thenReturn(Optional.of(testGenre));
        when(genreRepository.findGenreByName("Rock")).thenReturn(testGenre);
        when(genreRepository.save(any(Genre.class))).thenReturn(testGenre);

        Genre result = genreService.updateGenre(genreId, dto);

        assertEquals("Rock", result.getName());
        verify(genreRepository).save(testGenre);
    }
}