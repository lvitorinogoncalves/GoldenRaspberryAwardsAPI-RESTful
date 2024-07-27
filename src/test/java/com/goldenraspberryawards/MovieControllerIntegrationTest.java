package com.goldenraspberryawards;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goldenraspberryawards.model.Movie;
import com.goldenraspberryawards.service.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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
    }

    private void initializeMovieList() {
        movieList = movieService.getAllMovies();
        Mockito.reset(movieService);
    }

    @Test
    @Order(1)
    public void testGetAllMovies() throws Exception {
        mockMvc.perform(get("/api/movies/all"))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(movieList)));

        verify(movieService, times(1)).getAllMovies();
    }

    @Test
    @Order(2)
    public void testGetMovieById() throws Exception {
        Movie movie = movieList.get(0);

        mockMvc.perform(get("/api/movies/" + movie.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(movie)));

        verify(movieService, times(1)).getMovieById(movie.getId());
    }

    @Test
    @Order(3)
    public void testCreateMovie() throws Exception {
        Movie movie = new Movie(9999L, "Test Movie", "Test Studio", "Test Producer", 2020, true);

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
    @Order(4)
    public void testUpdateMovie() throws Exception {
        Movie movie = movieList.get(movieList.size() - 1);
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
    @Order(5)
    public void testDeleteMovie() throws Exception {
        Movie movie = movieList.get(movieList.size() - 1);

        mockMvc.perform(delete("/api/movies/" + movie.getId()))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(movieService, times(1)).deleteMovie(movie.getId());
    }

    @Test
    @Order(6)
    public void testGetMoviesWithPrizeIntervals() throws Exception {
        mockMvc.perform(get("/api/movies/producers-prize-intervals"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.min").isArray())
                .andExpect(jsonPath("$.min[0].producer").value("Joel Silver"))
                .andExpect(jsonPath("$.min[0].interval").value(1))
                .andExpect(jsonPath("$.min[0].previousWin").value(1990))
                .andExpect(jsonPath("$.min[0].followingWin").value(1991))
                .andExpect(jsonPath("$.max").isArray())
                .andExpect(jsonPath("$.max[0].producer").value("Matthew Vaughn"))
                .andExpect(jsonPath("$.max[0].interval").value(13))
                .andExpect(jsonPath("$.max[0].previousWin").value(2002))
                .andExpect(jsonPath("$.max[0].followingWin").value(2015));

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
