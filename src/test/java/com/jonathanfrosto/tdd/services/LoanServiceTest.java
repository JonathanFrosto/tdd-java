package com.jonathanfrosto.tdd.services;

import com.jonathanfrosto.tdd.TestConfig;
import com.jonathanfrosto.tdd.domain.dto.LoanDTO;
import com.jonathanfrosto.tdd.domain.entities.Book;
import com.jonathanfrosto.tdd.domain.entities.Loan;
import com.jonathanfrosto.tdd.exceptions.BusinessException;
import com.jonathanfrosto.tdd.repositories.BookRepository;
import com.jonathanfrosto.tdd.repositories.LoanRepository;
import com.jonathanfrosto.tdd.services.impl.LoanServiceImpl;
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
class LoanServiceTest {

    @Autowired
    LoanService loanService;

    @Autowired
    LoanRepository loanRepository;

    @Autowired
    BookRepository bookRepository;


    @TestConfiguration
    static class testConfiguration {
        @Autowired
        ModelMapper modelMapper;

        @MockBean
        LoanRepository loanRepository;

        @MockBean
        BookRepository bookRepository;

        @Bean
        LoanService getService() {
            return new LoanServiceImpl(modelMapper, loanRepository, bookRepository);
        }
    }

    @Test
    @DisplayName("Should save a loan")
    void shouldSaveLoan() {
        // Given
        when(bookRepository.findByIsbn(any(String.class))).thenReturn(Optional.of(new Book()));

        Loan savedLoan = Loan.builder().id(1L).build();
        when(loanRepository.save(any(Loan.class))).thenReturn(savedLoan);

        LoanDTO loanDTO = LoanDTO.builder().isbn("123").customer("jonathan").build();

        // When
        LoanDTO response = loanService.save(loanDTO);

        // Then
        assertThat(response.getId()).isEqualTo(savedLoan.getId());
    }

    @Test
    @DisplayName("Should not save a loan - already loaned")
    void shouldNotSaveLoanedBook() {
        // Given
        LoanDTO loanDTO = LoanDTO.builder().isbn("123").customer("jonathan").build();

        when(bookRepository.findByIsbn(any(String.class))).thenReturn(Optional.of(new Book()));
        when(loanRepository.existsByBookAndReturnedFalse(any(Book.class))).thenReturn(true);

        // When
        Throwable exception = catchThrowable(() -> loanService.save(loanDTO));

        // Then
        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Book already loaned");
    }

    @Test
    @DisplayName("Should give back a book of a loan")
    void shouldGiveBackBookFromLoan() {
        // Given
        Long id = 1L;

        Loan entity = Loan.builder().id(id).build();
        when(loanRepository.findById(id)).thenReturn(Optional.of(entity));

        // Then
        assertDoesNotThrow(() -> loanService.giveBackBook(id));
        verify(loanRepository, times(1)).save(entity);
    }

    @Test
    @DisplayName("Should not give back a book of a loan")
    void shouldNotGiveBackBookFromLoan() {
        Throwable exception = catchThrowable(() -> loanService.giveBackBook(1L));

        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Loan not found");
    }
}
