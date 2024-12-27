package com.finance.adam.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("api")
class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void testFinanceDataControllerGetApi() throws Exception {
        mockMvc.perform(get("/api/v1/finances/stocks"))
                .andExpect(status().isOk());
    }

    @Test
    void testHttpMediaTypeNotAcceptableException() throws Exception {
        mockMvc.perform(get("/api/v1/finances/stocks")
                .accept(MediaType.APPLICATION_XML_VALUE))
                .andExpect(status().isNotAcceptable());
    }
}