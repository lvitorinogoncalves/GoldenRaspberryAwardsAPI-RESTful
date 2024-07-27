package com.goldenraspberryawards;

import com.goldenraspberryawards.model.Movie;
import com.goldenraspberryawards.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class LoadDatabase {

    private static final String FILE_NAME_CSV = "src/main/resources/movielist.csv";
    private static final String TITLE_HEADER = "title";
    private static final String STUDIO_HEADER = "studios";
    private static final String PRODUCERS_HEADER = "producers";
    private static final String YEAR_HEADER = "year";
    private static final String WINNER_HEADER = "winner";

    @Autowired
    private MovieRepository movieRepository;

    @Bean
    CommandLineRunner initDatabase() {
        return args -> {
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(FILE_NAME_CSV))) {
                List<String> lines = bufferedReader.lines().collect(Collectors.toList());

                if (lines.isEmpty()) {
                    return;
                }

                String[] headerLine = {YEAR_HEADER, TITLE_HEADER, STUDIO_HEADER, PRODUCERS_HEADER, WINNER_HEADER};
                Map<String, Integer> headerMap = createHeaderMap(headerLine);

                lines.stream()
                        .skip(1)
                        .map(line -> createMovieFromLine(line, headerMap))
                        .forEach(movieRepository::save);

            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }

    private Map<String, Integer> createHeaderMap(String[] headers) {
        return Map.of(
                YEAR_HEADER.toLowerCase(), 0,
                TITLE_HEADER.toLowerCase(), 1,
                STUDIO_HEADER.toLowerCase(), 2,
                PRODUCERS_HEADER.toLowerCase(), 3,
                WINNER_HEADER.toLowerCase(), 4
        );
    }

    private Movie createMovieFromLine(String line, Map<String, Integer> headerMap) {
        String[] datasets = line.split(";");
        return new Movie(
                getValue(datasets, headerMap, TITLE_HEADER),
                getValue(datasets, headerMap, STUDIO_HEADER),
                getValue(datasets, headerMap, PRODUCERS_HEADER),
                parseYear(getValue(datasets, headerMap, YEAR_HEADER)),
                parseWinner(getValue(datasets, headerMap, WINNER_HEADER))
        );
    }

    private String getValue(String[] datasets, Map<String, Integer> headerMap, String header) {
        Integer index = headerMap.get(header.toLowerCase());
        return index != null && index < datasets.length ? datasets[index].trim() : "";
    }

    private int parseYear(String yearValue) {
        try {
            return Integer.parseInt(yearValue);
        } catch (NumberFormatException e) {
            System.err.println("Invalid year format: " + yearValue);
            return 0;
        }
    }

    private boolean parseWinner(String winnerValue) {
        return "yes".equalsIgnoreCase(winnerValue) ||
                "1".equalsIgnoreCase(winnerValue) ||
                "true".equalsIgnoreCase(winnerValue);
    }
}
