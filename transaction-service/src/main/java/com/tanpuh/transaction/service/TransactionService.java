package com.tanpuh.transaction.service;

import com.tanpuh.common.enums.TransactionType;
import com.tanpuh.transaction.dto.TransactionRequest;
import com.tanpuh.transaction.dto.TransactionResponse;
import com.tanpuh.transaction.dto.TransferTransactionRequest;

import java.time.LocalDate;
import java.util.List;

public interface TransactionService {
    List<TransactionResponse> getAllTransactions(TransactionType type, LocalDate startDate, LocalDate endDate);

    TransactionResponse getTransaction(Long id);

    TransactionResponse createTransaction(TransactionRequest request);

    TransactionResponse createTransaction(TransferTransactionRequest request);

    TransactionResponse updateTransaction(Long id, TransactionRequest request);

    void deleteTransaction(Long id);
}
