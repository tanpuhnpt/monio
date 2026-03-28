package com.mpt.monio.transaction.service;

import com.mpt.monio.transaction.dto.TransactionRequest;
import com.mpt.monio.transaction.dto.TransactionResponse;
import com.mpt.monio.transaction.dto.TransferTransactionRequest;

import java.time.LocalDate;
import java.util.List;

public interface TransactionService {
    List<TransactionResponse> getAllTransactions(LocalDate startDate, LocalDate endDate);

    TransactionResponse getTransaction(Long id);

    TransactionResponse createTransaction(TransactionRequest request);

    TransactionResponse createTransaction(TransferTransactionRequest request);

    TransactionResponse updateTransaction(Long id, TransactionRequest request);

    void deleteTransaction(Long id);
}
