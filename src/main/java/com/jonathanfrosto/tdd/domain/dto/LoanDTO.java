package com.jonathanfrosto.tdd.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanDTO {

    private Long id;

    @NotBlank
    private String isbn;

    @NotBlank
    private String customer;
}
