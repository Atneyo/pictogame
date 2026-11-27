package chloe.pictogame.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import chloe.pictogame.common.WordConstants;

// Controller -> service -> repository -> MySQL, nothing mocked
@SpringBootTest
@AutoConfigureMockMvc
class PictogameApplicationIT extends MySqlIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getRandomWords_returnsRequestedNumberOfWords() throws Exception {
        mockMvc.perform(get("/api/words/random").param("count", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].label").isString());
    }

    @Test
    void getRandomWords_withoutCount_returnsDefaultNumberOfWords() throws Exception {
        mockMvc.perform(get("/api/words/random"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(WordConstants.DEFAULT_COUNT));
    }

    @Test
    void getRandomWords_withCountAboveMaximum_returns400() throws Exception {
        mockMvc.perform(get("/api/words/random").param("count", String.valueOf(WordConstants.MAX_COUNT + 1)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_WORD_COUNT"));
    }

    @Test
    void getWords_withPagination_returnsRequestedPage() throws Exception {
        mockMvc.perform(get("/api/words").param("page", "1").param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nbOfPages").value(org.hamcrest.Matchers.greaterThanOrEqualTo(15)))
                .andExpect(jsonPath("$.pageNb").value(1))
                .andExpect(jsonPath("$.words.length()").value(2))
                .andExpect(jsonPath("$.words[0].id").isNumber())
                .andExpect(jsonPath("$.words[0].label").isString());
    }

    @Test
    void addWord_return_new_word() throws Exception {
        mockMvc.perform(post("/api/words").param("label", "test"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("label").value("test"));
    }
}