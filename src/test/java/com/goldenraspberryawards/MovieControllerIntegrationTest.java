package com.goldenraspberryawards;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goldenraspberryawards.model.Movie;
import com.goldenraspberryawards.service.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class MovieControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @SpyBean
    private MovieService movieService;

    private List<Movie> movieList;

    @BeforeEach
    public void setup() {
        initializeMovieList();
        setupMovieServiceMocks();
    }

    private void initializeMovieList() {
        movieList = new ArrayList<>();
        movieList.add(new Movie(1L, "Movie 1", "Studio 1", "Producer 1", 2000, false));
        movieList.add(new Movie(2L, "Movie 2", "Studio 2", "Producer 2", 2005, true));
        movieList.add(new Movie(3L, "Movie 2", "Studio 3", "Producer 2", 2010, true));
        movieList.add(new Movie(4L, "Movie 3", "Studio 3", "Producer 3, Producer 2 and Producer 1", 2015, true));
        movieList.add(new Movie(5L, "Movie 3", "Studio 3", "Producer 3", 2010, true));
        movieList.add(new Movie(6L, "Movie 3", "Studio 3", "Producer 3", 2000, false));
    }

    private void setupMovieServiceMocks() {
        doReturn(movieList).when(movieService).getAllMovies();
        doAnswer(invocation -> findMovieById(invocation.getArgument(0))).when(movieService).getMovieById(anyLong());
        doAnswer(invocation -> addMovie(invocation.getArgument(0))).when(movieService).createMovie(any(Movie.class));
        doAnswer(invocation -> updateMovie(invocation.getArgument(0), invocation.getArgument(1))).when(movieService).updateMovie(anyLong(), any(Movie.class));
        doAnswer(invocation -> deleteMovie(invocation.getArgument(0))).when(movieService).deleteMovie(anyLong());
    }

    private Optional<Movie> findMovieById(Long id) {
        return movieList.stream().filter(movie -> movie.getId().equals(id)).findFirst();
    }

    private Movie addMovie(Movie movie) {
        movieList.add(movie);
        return movie;
    }

    private Movie updateMovie(Long id, Movie updatedMovie) {
        movieList.replaceAll(movie -> movie.getId().equals(id) ? updatedMovie : movie);
        return updatedMovie;
    }

    private boolean deleteMovie(Long id) {
        return movieList.removeIf(movie -> movie.getId().equals(id));
    }

    @Test
    public void testGetAllMovies() throws Exception {
        mockMvc.perform(get("/api/movies/all"))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(movieList)));

        verify(movieService, times(1)).getAllMovies();
    }

    @Test
    public void testGetMovieById() throws Exception {
        Movie movie = movieList.get(0);

        mockMvc.perform(get("/api/movies/" + movie.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(movie)));

        verify(movieService, times(1)).getMovieById(movie.getId());
    }

    @Test
    public void testCreateMovie() throws Exception {
        Movie movie = new Movie(4L, "Test Movie", "Test Studio", "Test Producer", 2020, true);

        mockMvc.perform(post("/api/movies/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(movie)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(movie.getTitle()))
                .andExpect(jsonPath("$.studio").value(movie.getStudio()))
                .andExpect(jsonPath("$.producer").value(movie.getProducer()))
                .andExpect(jsonPath("$.year").value(movie.getYear()))
                .andExpect(jsonPath("$.winner").value(movie.isWinner()));

        verify(movieService, times(1)).createMovie(any(Movie.class));
    }

    @Test
    public void testUpdateMovie() throws Exception {
        Movie movie = movieList.get(0);
        Movie updatedMovie = new Movie(movie.getId(), "Updated Movie", "Updated Studio", "Updated Producer", 2021, false);

        mockMvc.perform(put("/api/movies/" + movie.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(updatedMovie)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(updatedMovie.getTitle()))
                .andExpect(jsonPath("$.studio").value(updatedMovie.getStudio()))
                .andExpect(jsonPath("$.producer").value(updatedMovie.getProducer()))
                .andExpect(jsonPath("$.year").value(updatedMovie.getYear()))
                .andExpect(jsonPath("$.winner").value(updatedMovie.isWinner()));

        verify(movieService, times(1)).updateMovie(eq(movie.getId()), any(Movie.class));
    }

    @Test
    public void testDeleteMovie() throws Exception {
        Movie movie = movieList.get(0);

        mockMvc.perform(delete("/api/movies/" + movie.getId()))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(movieService, times(1)).deleteMovie(movie.getId());
    }

    @Test
    public void testGetMoviesWithPrizeIntervals() throws Exception {
        mockMvc.perform(get("/api/movies/producers-prize-intervals"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(movieService, times(1)).getMoviesWithPrizeIntervals();
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
