package com.goldenraspberryawards.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MovieProducerPrizeInterval {

    private String producer;
    private int interval;
    private int previousWin;
    private int followingWin;

    public MovieProducerPrizeInterval(String producer, int previousWin, int followingWin) {
        this.producer = producer;
        this.previousWin = previousWin;
        this.followingWin = followingWin;
        this.interval = followingWin - previousWin;
    }
}
