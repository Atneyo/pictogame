package chloe.pictogame.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import chloe.pictogame.common.WordConstants;

// Controller -> service -> repository -> MySQL, nothing mocked
@SpringBootTest
@AutoConfigureMockMvc
class PictogameApplicationIT extends MySqlIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getWords_returnsRequestedNumberOfWords() throws Exception {
        mockMvc.perform(get("/api/words").param("count", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].label").isString());
    }

    @Test
    void getWords_withoutCount_returnsDefaultNumberOfWords() throws Exception {
        mockMvc.perform(get("/api/words"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(WordConstants.DEFAULT_COUNT));
    }

    @Test
    void getWords_withCountAboveMaximum_returns400() throws Exception {
        mockMvc.perform(get("/api/words").param("count", String.valueOf(WordConstants.MAX_COUNT + 1)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_WORD_COUNT"));
    }
}