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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME_CSV))) {
                ArrayList<String> lines = new ArrayList<>();
                String line;

                while ((line = br.readLine()) != null) {
                    lines.add(line);
                }

                if (lines.isEmpty()) {
                    return;
                }

                String[] headerLine = {YEAR_HEADER, TITLE_HEADER, STUDIO_HEADER, PRODUCERS_HEADER, WINNER_HEADER};
                Map<String, Integer> headerMap = createHeaderMap(headerLine);

                for (int i = 1; i < lines.size(); i++) {
                    Movie movie = createMovieFromline(lines.get(i), headerMap);
                    movieRepository.save(movie);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }

    private Map<String, Integer> createHeaderMap(String[] headers) {
        Map<String, Integer> headerMap = new HashMap<>();
        for (int i = 0; i < headers.length; i++) {
            headerMap.put(headers[i].trim().toLowerCase(), i);
        }
        return headerMap;
    }

    private Movie createMovieFromline(String line, Map<String, Integer> headerMap) {
        Movie movie = new Movie();
        String[] datasets = line.split(";");

        movie.setTitle(getValue(datasets, headerMap, TITLE_HEADER));
        movie.setStudio(getValue(datasets, headerMap, STUDIO_HEADER));
        movie.setProducer(getValue(datasets, headerMap, PRODUCERS_HEADER));
        movie.setYear(parseYear(getValue(datasets, headerMap, YEAR_HEADER)));
        movie.setWinner(parseWinner(getValue(datasets, headerMap, WINNER_HEADER)));

        return movie;
    }

    private String getValue(String[] datasets, Map<String, Integer> headerMap, String header) {
        Integer index = headerMap.get(header);
        if (index != null && index < datasets.length) {
            return datasets[index].trim();
        }
        return "";
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
