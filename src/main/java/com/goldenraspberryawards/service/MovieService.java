package com.goldenraspberryawards.service;

import com.goldenraspberryawards.model.Movie;
import com.goldenraspberryawards.model.MovieProducerPrizeInterval;
import com.goldenraspberryawards.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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

    private List<MovieProducerPrizeInterval> createProducerPrizeIntervals(List<Movie> movies) {
        return movies.stream()
                .collect(Collectors.groupingBy(Movie::getProducer))
                .entrySet().stream()
                .map(entry -> {
                    String producer = entry.getKey();
                    List<Movie> sortedMovies = entry.getValue().stream()
                            .sorted(Comparator.comparingInt(Movie::getYear))
                            .collect(Collectors.toList());

                    OptionalInt minYearOpt = sortedMovies.stream().mapToInt(Movie::getYear).findFirst();
                    OptionalInt maxYearOpt = sortedMovies.stream().mapToInt(Movie::getYear).reduce((first, last) -> last);

                    if (minYearOpt.isPresent() && maxYearOpt.isPresent() && minYearOpt.getAsInt() != maxYearOpt.getAsInt()) {
                        int minYear = minYearOpt.getAsInt();
                        int maxYear = maxYearOpt.getAsInt();
                        return new MovieProducerPrizeInterval(producer, minYear, maxYear);
                    }

                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    public Movie getMovieById(Long id) {
        return movieRepository.findById(id).orElse(null);
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
