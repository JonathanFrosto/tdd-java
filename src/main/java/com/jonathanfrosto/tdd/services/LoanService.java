package com.jonathanfrosto.tdd.services;

import com.jonathanfrosto.tdd.domain.dto.LoanDTO;
import com.jonathanfrosto.tdd.domain.dto.LoanFilterDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LoanService {

    LoanDTO save(LoanDTO loan);

    void giveBackBook(Long id);

    Page<LoanDTO> find(LoanFilterDTO loanFilterDTO, Pageable pageable);
}
