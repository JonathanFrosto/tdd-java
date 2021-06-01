package com.jonathanfrosto.tdd.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jonathanfrosto.tdd.domain.dto.BookDTO;
import com.jonathanfrosto.tdd.exceptions.BusinessException;
import com.jonathanfrosto.tdd.services.BookService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = BookController.class)
@AutoConfigureMockMvc
class BookControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BookService bookService;

    @Test
    @DisplayName("Should create a book")
    void registerBook() throws Exception {
        String content = new ObjectMapper().writeValueAsString(getBookDTO());

        BookDTO response = BookDTO.builder()
                .id(1L)
                .name("A alcateia")
                .author("Jonathan Anthony")
                .isbn("123")
                .build();

        when(bookService.save(any(BookDTO.class))).thenReturn(response);

        mockMvc.perform(postRequest(content))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("A alcateia")))
                .andExpect(jsonPath("$.isbn", is("123")))
                .andExpect(jsonPath("$.author", is("Jonathan Anthony")));
    }

    @Test
    @DisplayName("Fail - mandatory fields")
    void failRegisterBook() throws Exception {
        String content = new ObjectMapper().writeValueAsString(new BookDTO());

        mockMvc.perform(postRequest(content))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(3)));
    }

    @Test
    @DisplayName("Conflict - Duplicated isbn")
    void failDuplicatedIsbn() throws Exception {
        String content = new ObjectMapper().writeValueAsString(getBookDTO());

        when(bookService.save(any(BookDTO.class))).thenThrow(new BusinessException("Duplicated isbn", 409));

        mockMvc.perform(postRequest(content))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors.[0].message", is("Duplicated isbn")));
    }

    @Test
    @DisplayName("Should get a book by id")
    void shouldGetBookById() throws Exception {
        Long idBook = 1L;

        BookDTO response = getBookDTO();
        response.setId(idBook);

        when(bookService.getById(idBook)).thenReturn(response);

        mockMvc.perform(get("/book/" + idBook))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(idBook.intValue())))
                .andExpect(jsonPath("$.name", is("A alcateia")))
                .andExpect(jsonPath("$.author", is("Jonathan Anthony")))
                .andExpect(jsonPath("$.isbn", is("123")));
    }

    @Test
    @DisplayName("Should not get a book by id")
    void shouldNotGetBookById() throws Exception {
        Long idBook = 2L;

        when(bookService.getById(idBook)).thenThrow(new BusinessException("Book not found", 404));

        mockMvc.perform(get("/book/" + idBook))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors.[0].message", is("Book not found")));
    }

    @Test
    @DisplayName("Should delete a book")
    void shouldDeleteBookById() throws Exception {
        mockMvc.perform(delete("/book/1"))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should not delete a book")
    void shouldNotDeleteBookById() throws Exception {
        doThrow(new BusinessException("Book not found", 404))
                .when(bookService)
                .delete(anyLong());

        mockMvc.perform(delete("/book/2"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should update a book")
    void ShouldUpdateBook() throws Exception {
        Long id = 1L;

        BookDTO toUpdate = getBookDTO();
        toUpdate.setId(id);

        String body = new ObjectMapper().writeValueAsString(toUpdate);
        when(bookService.update(toUpdate)).thenReturn(toUpdate);

        mockMvc.perform(putRequest(id, body))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id.intValue())))
                .andExpect(jsonPath("$.name", is(toUpdate.getName())))
                .andExpect(jsonPath("$.author", is(toUpdate.getAuthor())))
                .andExpect(jsonPath("$.isbn", is(toUpdate.getIsbn())));
    }

    @Test
    @DisplayName("Should not update a book")
    void ShouldNotUpdateBook() throws Exception {
        Long id = 2L;

        BookDTO toUpdate = getBookDTO();
        toUpdate.setId(id);

        String body = new ObjectMapper().writeValueAsString(toUpdate);

        when(bookService.update(toUpdate))
                .thenThrow(new BusinessException("Book not found", 404));

        mockMvc.perform(putRequest(id, body))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Get book with filter")
    void SholdGetBookByFilter() throws Exception {
        Long id = 1L;
        BookDTO bookDTO = getBookDTO();
        bookDTO.setId(id);

        when(bookService.find(any(BookDTO.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(singletonList(bookDTO), PageRequest.of(0, 100), 1L));

        String filter = String.format("?title=%s&author=%s&page=0size=100",
                bookDTO.getName(), bookDTO.getAuthor());

        mockMvc.perform(get("/book".concat(filter)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.totalElements", is(1)))
                .andExpect(jsonPath("$.pageable.pageNumber", is(0)))
                .andExpect(jsonPath("$.pageable.pageSize", is(100)));
    }

    private BookDTO getBookDTO() {
        return BookDTO.builder()
                .name("A alcateia")
                .author("Jonathan Anthony")
                .isbn("123")
                .build();
    }

    private MockHttpServletRequestBuilder postRequest(String content) {
        return MockMvcRequestBuilders
                .post("/book")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);
    }

    private MockHttpServletRequestBuilder putRequest(Long id, String body) {
        return MockMvcRequestBuilders
                .put("/book/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(body);
    }
}
