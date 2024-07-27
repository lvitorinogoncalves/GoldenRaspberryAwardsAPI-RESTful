package com.goldenraspberryawards.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;
    private String studio;
    private String producer;

    @Column(name = "release_year")
    private Integer year;
    private boolean winner;

    public Movie(String title, String studio, String producer, int year, boolean winner) {
        this.title = title;
        this.studio = studio;
        this.producer = producer;
        this.year = year;
        this.winner = winner;
    }
}
