package chloe.pictogame.endpoint;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.CannotCreateTransactionException;

import chloe.pictogame.common.WordConstants;
import chloe.pictogame.dto.WordResponse;
import chloe.pictogame.exception.InvalidWordCountException;
import chloe.pictogame.service.WordService;

// Loads only the web layer (controller + GlobalExceptionHandler), the service is a mock
@WebMvcTest(ApiController.class)
class  ApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WordService wordService;

    @Test
    void getWords_returnsWordsAsJson() throws Exception {
        // Prepare
        when(wordService.getRandomWords(2))
                .thenReturn(List.of(new WordResponse(1L, "chat"), new WordResponse(2L, "chien")));

        // Execute
        mockMvc.perform(get("/api/words").param("count", "2"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].label").value("chat"));

        // Control
    }

    @Test
    void getWords_withoutCount_usesDefaultCount() throws Exception {
        // Prepare
        when(wordService.getRandomWords(WordConstants.DEFAULT_COUNT)).thenReturn(List.of());

        // Execute
        mockMvc.perform(get("/api/words"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

        // Control
        verify(wordService).getRandomWords(WordConstants.DEFAULT_COUNT);
    }

    @Test
    void getWords_invalidCount_returns400WithErrorCode() throws Exception {
        // Prepare
        when(wordService.getRandomWords(0))
                .thenThrow(new InvalidWordCountException(0, WordConstants.MIN_COUNT, WordConstants.MAX_COUNT));

        // Execute
        mockMvc.perform(get("/api/words").param("count", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("INVALID_WORD_COUNT"))
                .andExpect(jsonPath("$.instance").value("/api/words"))
                .andExpect(jsonPath("$.timestamp").exists());

        // Control
    }

    @Test
    void getWords_nonNumericCount_returns400WithoutCallingService() throws Exception {
        // Prepare

        // Execute
        mockMvc.perform(get("/api/words").param("count", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_PARAMETER"));

        // Control
        verifyNoInteractions(wordService);
    }

    @Test
    void unknownUrl_returns404() throws Exception {
        // Prepare

        // Execute
        mockMvc.perform(get("/api/unknown"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));

        // Control
    }

    @Test
    void wrongHttpMethod_returns405() throws Exception {
        // Prepare

        // Execute
        mockMvc.perform(post("/api/words"))
                .andExpect(status().isMethodNotAllowed());

        // Control
    }

    @Test
    void databaseDown_returns503WithoutLeakingDetails() throws Exception {
        // Prepare
        when(wordService.getRandomWords(3)).thenThrow(new CannotCreateTransactionException("jdbc secret-host:3306"));

        // Execute
        mockMvc.perform(get("/api/words").param("count", "3"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.code").value("DATABASE_UNAVAILABLE"))
                .andExpect(jsonPath("$.detail").value(not(containsString("secret-host"))));

        // Control
    }

    @Test
    void unexpectedError_returns500WithoutLeakingDetails() throws Exception {
        // Prepare
        when(wordService.getRandomWords(3)).thenThrow(new IllegalStateException("internal secret detail"));

        // Execute
        mockMvc.perform(get("/api/words").param("count", "3"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("INTERNAL_ERROR"))
                .andExpect(jsonPath("$.detail").value(not(containsString("internal secret detail"))));

        // Control
    }
}