package com.jonathanfrosto.tdd.services;

import com.jonathanfrosto.tdd.TestConfig;
import com.jonathanfrosto.tdd.domain.dto.BookDTO;
import com.jonathanfrosto.tdd.domain.entities.Book;
import com.jonathanfrosto.tdd.exceptions.BusinessException;
import com.jonathanfrosto.tdd.repositories.BookRepository;
import com.jonathanfrosto.tdd.services.impl.BookServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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

    @Test
    @DisplayName("Should save a book")
    void saveBook() {
        // Given
        Book repositoryResponse = getRepositoryBook();

        when(bookRepository.save(any(Book.class))).thenReturn(repositoryResponse);

        // When
        BookDTO response = bookService.save(getBookDTO());

        // Then
        assertThat(response.getId()).isEqualTo(repositoryResponse.getId());
        assertThat(response.getAuthor()).isEqualTo(repositoryResponse.getAuthor());
        assertThat(response.getName()).isEqualTo(repositoryResponse.getName());
        assertThat(response.getIsbn()).isEqualTo(repositoryResponse.getIsbn());
    }

    @Test
    @DisplayName("Conflict - Duplicated isbn")
    void ShouldNotSaveBookDuplicatedIsbn() {
        // Given
        when(bookRepository.existsByIsbn(any(String.class))).thenReturn(true);

        // When
        Throwable exception = Assertions.catchThrowable(() -> bookService.save(getBookDTO()));

        //Then
        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Duplicated isbn");

        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    @DisplayName("Should get a book by id")
    void ShouldGetBookById() {
        Long id = 1L;
        Book repositoryResponse = getRepositoryBook();

        // Given
        when(bookRepository.findById(id)).thenReturn(Optional.of(repositoryResponse));

        // When
        BookDTO serviceResponse = bookService.getById(id);

        //Then
        assertThat(serviceResponse.getId()).isEqualTo(id);
        assertThat(serviceResponse.getName()).isEqualTo(repositoryResponse.getName());
        assertThat(serviceResponse.getAuthor()).isEqualTo(repositoryResponse.getAuthor());
        assertThat(serviceResponse.getIsbn()).isEqualTo(repositoryResponse.getIsbn());
    }

    @Test
    @DisplayName("Should not get a book by id")
    void ShouldNotGetBookById() {
        // Given
        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When
        Throwable exception = Assertions.catchThrowable(() -> bookService.getById(2L));

        //Then
        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Book not found");
    }

    @Test
    @DisplayName("Should delete a book")
    void ShouldDeleteBook() {
        long id = 1L;

        Book entity = getRepositoryBook();
        when(bookRepository.findById(id)).thenReturn(Optional.of(entity));

        bookService.delete(id);

        assertDoesNotThrow(() -> BusinessException.class);

        verify(bookRepository, times(1)).delete(entity);
    }

    @Test
    @DisplayName("Should not delete a book")
    void ShouldNotDeleteBook() {
        Long id = null;

        Throwable exception = catchThrowable(() -> bookService.delete(id));

        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Book not found");

        verify(bookRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Should update a book")
    void ShouldUpdateBook() {
        BookDTO bookDTO = getBookDTO();
        bookDTO.setId(1L);

        Book entity = getRepositoryBook();
        when(bookRepository.findById(any())).thenReturn(Optional.of(entity));

        BookDTO updated = bookService.update(bookDTO);

        assertDoesNotThrow(() -> BusinessException.class);

        verify(bookRepository, times(1)).save(entity);

        assertThat(updated.getId()).isEqualTo(bookDTO.getId());
        assertThat(updated.getName()).isEqualTo(bookDTO.getName());
        assertThat(updated.getAuthor()).isEqualTo(bookDTO.getAuthor());
        assertThat(updated.getIsbn()).isEqualTo(bookDTO.getIsbn());
    }

    @Test
    @DisplayName("Should not update a book")
    void ShouldNotUpdateBook() {
        BookDTO bookDTO = getBookDTO();
        bookDTO.setId(2L);

        when(bookRepository.findById(any())).thenReturn(Optional.empty());

        Throwable exception = catchThrowable(() -> bookService.update(bookDTO));

        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Book not found");

        verify(bookRepository, never()).save(any());
    }



    private Book getRepositoryBook() {
        return Book.builder()
                .id(1L)
                .name("A alcateia")
                .author("Jonathan Anthony")
                .isbn("123")
                .build();
    }

    private BookDTO getBookDTO() {
        return BookDTO.builder()
                .name("A alcateia")
                .author("Jonathan Anthony")
                .isbn("123")
                .build();
    }

}
