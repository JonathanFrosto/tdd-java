package com.jonathanfrosto.tdd.services;

import com.jonathanfrosto.tdd.domain.dto.LoanDTO;

public interface LoanService {

    LoanDTO save(LoanDTO loan);
}
