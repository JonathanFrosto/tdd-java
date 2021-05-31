package com.jonathanfrosto.tdd.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jonathanfrosto.tdd.domain.dto.LoanDTO;
import com.jonathanfrosto.tdd.exceptions.BusinessException;
import com.jonathanfrosto.tdd.services.LoanService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = LoanController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
class LoanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoanService loanService;

    private static final String LOAN_API = "/loans";

    @Test
    @DisplayName("Should borrow a book")
    void borrowBook() throws Exception {

        LoanDTO loanDTO = LoanDTO.builder().isbn("123").customer("Person").build();

        String json = new ObjectMapper().writeValueAsString(loanDTO);

        LoanDTO response = LoanDTO.builder()
                .id(1L)
                .isbn(loanDTO.getIsbn())
                .customer(loanDTO.getCustomer())
                .build();

        given(loanService.save(any(LoanDTO.class))).willReturn(response);

        mockMvc.perform(postRequest(json))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").isNotEmpty());
    }

    @Test
    @DisplayName("Fail - mandatory fields")
    void failRegisterBook() throws Exception {
        String content = new ObjectMapper().writeValueAsString(new LoanDTO());

        mockMvc.perform(postRequest(content))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(2)));
    }

    @Test
    @DisplayName("Shoud not borrow a book - book not found")
    void borrowBookThenNotFind() throws Exception {
        // Given
        LoanDTO request = LoanDTO.builder().isbn("999").customer("Person").build();

        given(loanService.save(request)).willThrow(new BusinessException("Book not found", 404));

        String json = new ObjectMapper().writeValueAsString(request);

        // When
        mockMvc.perform(postRequest(json))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors.[0].message", is("Book not found")));
    }

    private MockHttpServletRequestBuilder postRequest(String json) {
        return MockMvcRequestBuilders.post(LOAN_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);
    }
}