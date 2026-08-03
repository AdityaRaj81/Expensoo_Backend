package com.expenso.expense_tracker.mapper;

import com.expenso.expense_tracker.dto.transaction.TransactionDTO;
import com.expenso.expense_tracker.dto.transaction.TransactionResponse;
import com.expenso.expense_tracker.model.Transaction;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface TransactionMapper {

    /**
     * Entity -> DTO
     * Used for Dashboard & Transaction List
     */
    TransactionDTO toTransactionDTO(Transaction transaction);

    /**
     * Entity -> Response
     * Used for Transaction Details
     */
    TransactionResponse toTransactionResponse(Transaction transaction);

    /**
     * Entity List -> DTO List
     */
    List<TransactionDTO> toTransactionDTOList(List<Transaction> transactions);

    /**
     * Entity List -> Response List
     */
    List<TransactionResponse> toTransactionResponseList(List<Transaction> transactions);

}