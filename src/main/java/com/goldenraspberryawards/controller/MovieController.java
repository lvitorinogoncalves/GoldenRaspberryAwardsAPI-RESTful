package com.goldenraspberryawards.controller;

import com.goldenraspberryawards.model.Movie;
import com.goldenraspberryawards.model.MovieProducerPrizeInterval;
import com.goldenraspberryawards.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    @Autowired
    private MovieService movieService;

    @GetMapping("/initialized")
    public String getInitialized() {
        return "Initialized";
    }

    @GetMapping("/producers-prize-intervals")
    public ResponseEntity<Map<String, List<MovieProducerPrizeInterval>>> getMoviesWithPrizeIntervals() {
        Map<String, List<MovieProducerPrizeInterval>> moviesWithPrizeIntervals = movieService.getMoviesWithPrizeIntervals();
        return ResponseEntity.ok(moviesWithPrizeIntervals);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Movie>> getAllMovies() {
        List<Movie> moves = movieService.getAllMovies();
        return ResponseEntity.ok(moves);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Movie> getMovieById(@PathVariable Long id) {
        Movie movie = movieService.getMovieById(id);
        return movie != null ? ResponseEntity.ok(movie) : ResponseEntity.notFound().build();
    }

    @PostMapping("/create")
    public ResponseEntity<Movie> createMovie(@RequestBody Movie movie) {
        Movie createdMovie = movieService.createMovie(movie);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMovie);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Movie> updateMovie(@PathVariable Long id, @RequestBody Movie movieDetails) {
        Movie updatedMovie = movieService.updateMovie(id, movieDetails);
        return updatedMovie != null ? ResponseEntity.ok(updatedMovie) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        boolean isDeleted = movieService.deleteMovie(id);
        return isDeleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
