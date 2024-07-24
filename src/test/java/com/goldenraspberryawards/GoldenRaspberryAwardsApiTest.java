package com.goldenraspberryawards;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class GoldenRaspberryAwardsApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getTestShouldReturnInitialized() throws Exception {
        this.mockMvc.perform(get("/api/producers/test"))
                .andExpect(status().isOk())
                .andExpect(content().string("Initialized"));
    }
}
