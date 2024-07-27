package com.goldenraspberryawards.service;

import com.goldenraspberryawards.model.Movie;
import com.goldenraspberryawards.model.MovieProducerPrizeInterval;
import com.goldenraspberryawards.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class MovieService {

    @Autowired
    private MovieRepository movieRepository;

    public Map<String, List<MovieProducerPrizeInterval>> getMoviesWithPrizeIntervals() {
        return findProducersWithLargestOrSmallestGap();
    }

    private Map<String, List<MovieProducerPrizeInterval>> findProducersWithLargestOrSmallestGap() {
        List<Movie> moviesByProducersWinners = expandMoviesByProducers();
        List<Movie> moviesWithMoreThanOneAward = filterMoviesWithMoreThanOneAward(moviesByProducersWinners);
        List<MovieProducerPrizeInterval> moviesProducersPrizesIntervals = createProducerPrizeIntervals(moviesWithMoreThanOneAward);

        int minIntervalValue = moviesProducersPrizesIntervals.stream()
                .mapToInt(MovieProducerPrizeInterval::getInterval)
                .min()
                .orElse(Integer.MAX_VALUE);

        int maxIntervalValue = moviesProducersPrizesIntervals.stream()
                .mapToInt(MovieProducerPrizeInterval::getInterval)
                .max()
                .orElse(Integer.MIN_VALUE);

        List<MovieProducerPrizeInterval> minIntervals = moviesProducersPrizesIntervals.stream()
                .filter(interval -> interval.getInterval() == minIntervalValue)
                .collect(Collectors.toList());

        List<MovieProducerPrizeInterval> maxIntervals = moviesProducersPrizesIntervals.stream()
                .filter(interval -> interval.getInterval() == maxIntervalValue)
                .collect(Collectors.toList());

        Map<String, List<MovieProducerPrizeInterval>> result = new HashMap<>();

        result.put("min", minIntervals);
        result.put("max", maxIntervals);

        return result;
    }

    private List<Movie> expandMoviesByProducers() {
        return getAllMovies().stream()
                .filter(Movie::isWinner)
                .flatMap(movie ->
                        Arrays.stream(movie.getProducer().split(",\\s*|\\s+and\\s+"))
                                .map(producer ->
                                        new Movie(movie.getId(), movie.getTitle(), movie.getStudio(),
                                                producer.trim(), movie.getYear(), movie.isWinner()
                                        )
                                )
                )
                .collect(Collectors.toList());
    }

    private List<Movie> filterMoviesWithMoreThanOneAward(List<Movie> movies) {
        return movies.stream()
                .collect(Collectors.groupingBy(Movie::getProducer, Collectors.counting()))
                .entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .flatMap(entry -> movies.stream().filter(movie -> movie.getProducer().equals(entry.getKey())))
                .collect(Collectors.toList());
    }

    public List<MovieProducerPrizeInterval> createProducerPrizeIntervals(List<Movie> movies) {
        List<MovieProducerPrizeInterval> intervals = new ArrayList<>();

        Map<String, List<Movie>> moviesByProducer = movies.stream()
                .collect(Collectors.groupingBy(Movie::getProducer));

        moviesByProducer.forEach((producer, producerMovies) -> {
            List<Integer> sortedYears = producerMovies.stream()
                    .map(Movie::getYear)
                    .sorted()
                    .toList();

            intervals.addAll(createIntervals(producer, sortedYears));
        });

        return intervals;
    }

    private List<MovieProducerPrizeInterval> createIntervals(String producer, List<Integer> sortedYears) {
        return (sortedYears.size() < 2) ? List.of() :
                IntStream.range(1, sortedYears.size())
                        .mapToObj(i -> new MovieProducerPrizeInterval(producer, sortedYears.get(i - 1), sortedYears.get(i)))
                        .toList();
    }

    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    public Optional<Movie> getMovieById(Long id) {
        return movieRepository.findById(id);
    }

    public Movie createMovie(Movie movie) {
        return movieRepository.save(movie);
    }

    public Movie updateMovie(Long id, Movie movie) {
        Optional<Movie> optionalMovie = movieRepository.findById(id);
        if (optionalMovie.isPresent()) {
            Movie movieData = optionalMovie.get();
            movieData.setTitle(movie.getTitle());
            movieData.setStudio(movie.getStudio());
            movieData.setProducer(movie.getProducer());
            movieData.setYear(movie.getYear());
            movieData.setWinner(movie.isWinner());
            return movieRepository.save(movieData);
        }
        return null;
    }

    public boolean deleteMovie(Long id) {
        if (movieRepository.existsById(id)) {
            movieRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
