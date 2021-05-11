package com.jonathanfrosto.tdd.services;

import com.jonathanfrosto.tdd.TestConfig;
import com.jonathanfrosto.tdd.domain.dto.BookDTO;
import com.jonathanfrosto.tdd.domain.entities.Book;
import com.jonathanfrosto.tdd.exceptions.BusinessException;
import com.jonathanfrosto.tdd.repositories.BookRepository;
import com.jonathanfrosto.tdd.services.impl.BookServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@Import(TestConfig.class)
class BookServiceTest {

    @Autowired
    BookService bookService;

    @Autowired
    BookRepository bookRepository;

    private BookDTO book;

    @TestConfiguration
    static class configure {
        @Autowired
        ModelMapper modelMapper;

        @MockBean
        BookRepository bookRepository;

        @Bean
        BookService getBookService() {
            return new BookServiceImpl(bookRepository, modelMapper);
        }
    }

    @BeforeEach
    void setUp() {
        book = BookDTO.builder()
                .name("A alcateia")
                .author("Jonathan Anthony")
                .isbn("123")
                .build();
    }

    @Test
    @DisplayName("Should save a book")
    void saveBook() {
        // Given
        Book repositoryResponse = Book.builder()
                .id(1L)
                .name("A alcateia")
                .author("Jonathan Anthony")
                .isbn("123")
                .build();

        when(bookRepository.save(any(Book.class))).thenReturn(repositoryResponse);

        // When
        BookDTO response = bookService.save(book);

        // Then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getAuthor()).isEqualTo("Jonathan Anthony");
        assertThat(response.getName()).isEqualTo("A alcateia");
        assertThat(response.getIsbn()).isEqualTo("123");
    }

    @Test
    @DisplayName("Conflict - Duplicated isbn")
    void ShouldNotSaveBookDuplicatedIsbn() {
        // Given
        when(bookRepository.existsByIsbn(any(String.class))).thenReturn(true);

        // When
        Throwable exception = Assertions.catchThrowable(() -> bookService.save(book));

        //Then
        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Duplicated isbn");

        verify(bookRepository, never()).save(any(Book.class));
    }
}
