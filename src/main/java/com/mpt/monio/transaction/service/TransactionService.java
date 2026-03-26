package com.mpt.monio.transaction.service;

import com.mpt.monio.transaction.dto.TransactionRequest;
import com.mpt.monio.transaction.dto.TransactionResponse;

import java.util.List;

public interface TransactionService {
    List<TransactionResponse> getAllTransactions();

    TransactionResponse getTransaction(Long id);

    TransactionResponse createTransaction(TransactionRequest request);

    TransactionResponse updateTransaction(Long id, TransactionRequest request);

    void deleteTransaction(Long id);
}
