package com.tanpuh.transaction.service;

import com.tanpuh.common.exception.AppException;
import com.tanpuh.common.exception.ErrorCode;
import com.tanpuh.common.util.DateRange;
import com.tanpuh.common.util.SecurityContext;
import com.tanpuh.transaction.dto.*;
import com.tanpuh.transaction.entity.Transaction;
import com.tanpuh.common.enums.TransactionType;
import com.tanpuh.transaction.mapper.TransactionMapper;
import com.tanpuh.transaction.repo.CategoryClient;
import com.tanpuh.transaction.repo.TransactionRepository;
import com.tanpuh.transaction.repo.WalletClient;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class TransactionServiceImpl implements TransactionService {
    TransactionMapper mapper;
    TransactionRepository transactionRepository;
    CategoryClient categoryClient;
    WalletClient walletClient;

    @Override
    public List<TransactionResponse> getAllTransactions(TransactionType type, LocalDate startDate, LocalDate endDate) {
        Long userId = SecurityContext.getCurrentUserId();

        // handle input: type
        List<TransactionType> types = (type == null)
                ? List.of(TransactionType.INCOME, TransactionType.EXPENSE)
                : List.of(type);

        // handle input date
        DateRange dateRange = DateRange.ofCurrentMonthIfNull(startDate, endDate);

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");

        List<Transaction> transactions = transactionRepository
                .findAllByUserId(userId, types, dateRange.start(), dateRange.end(), sort);

        if (transactions.isEmpty()) return List.of();

        // map to response
        List<Long> categoryIds = transactions.stream()
                .map(Transaction::getCategoryId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        List<Long> walletIds = transactions.stream()
                .flatMap(t -> Stream.of(t.getWalletId(), t.getDestinationWalletId()))
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        Map<Long, CategoryResponse> categoryMap = categoryIds.isEmpty()
                ? Map.of()
                : categoryClient.getCategoriesByIds(categoryIds).stream()
                .collect(Collectors.toMap(CategoryResponse::id, c -> c));

        Map<Long, WalletResponse> walletMap = walletIds.isEmpty()
                ? Map.of()
                : walletClient.getWalletsByIds(walletIds).stream()
                .collect(Collectors.toMap(WalletResponse::id, w -> w));

        return transactions.stream()
                .map(entity -> mapper.toResponse(
                        entity,
                        entity.getCategoryId() != null ? categoryMap.get(entity.getCategoryId()) : null,
                        entity.getWalletId() != null ? walletMap.get(entity.getWalletId()) : null,
                        entity.getDestinationWalletId() != null ? walletMap.get(entity.getDestinationWalletId()) : null
                ))
                .toList();
    }

    @Override
    public TransactionResponse getTransaction(Long id) {
        Long userId = SecurityContext.getCurrentUserId();

        Transaction transaction = transactionRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new AppException(ErrorCode.TRANSACTION_NOT_FOUND));

        // map to response
        CategoryResponse categoryResponse = transaction.getCategoryId() == null
                ? null
                : categoryClient.getCategoryById(transaction.getCategoryId());

        WalletResponse walletResponse = transaction.getWalletId() == null
                ? null
                : walletClient.getWallet(transaction.getWalletId());

        WalletResponse destinationWalletResponse = transaction.getDestinationWalletId() == null
                ? null
                : walletClient.getWallet(transaction.getDestinationWalletId());

        return mapper.toResponse(transaction, categoryResponse, walletResponse, destinationWalletResponse);
    }

    // check tồn tại
    @Override
    @Transactional
    public TransactionResponse createTransaction(TransactionRequest request) {
        Long userId = SecurityContext.getCurrentUserId();

        Transaction transaction = mapper.toEntity(request);
        transaction.setUserId(userId);
        transaction.setCategoryId(request.categoryId());
        transaction.setWalletId(request.walletId());
        transactionRepository.save(transaction);

        // update balance in wallet
        var updateBalanceRequest = new UpdateBalanceRequest(request.walletId(), request.type(), request.amount());
        walletClient.updateBalanceByTransactionType(updateBalanceRequest, false);

        // map to response
        CategoryResponse categoryResponse = categoryClient.getCategoryById(request.categoryId());
        WalletResponse walletResponse = walletClient.getWallet(request.walletId());
        return mapper.toResponse(transaction, categoryResponse, walletResponse, null);
    }

    @Override
    @Transactional
    public TransactionResponse createTransaction(TransferTransactionRequest request) {
        Long userId = SecurityContext.getCurrentUserId();

        if (Objects.equals(request.sourceWalletId(), request.destinationWalletId()))
            throw new AppException(ErrorCode.TRANSACTION_DESTINATION_WALLET_INVALID);

        Transaction transaction = mapper.toEntity(request);
        transaction.setType(TransactionType.TRANSFER);
        transaction.setUserId(userId);
        transaction.setWalletId(request.sourceWalletId());
        transaction.setDestinationWalletId(request.destinationWalletId());
        transactionRepository.save(transaction);

        // update balance in wallets
        var transferRequest = new TransferRequest(request.sourceWalletId(), request.destinationWalletId(), request.amount());
        walletClient.updateBalances(transferRequest);

        // map to response
        WalletResponse sourceWalletResponse = walletClient.getWallet(request.sourceWalletId());
        WalletResponse destinationWalletResponse = walletClient.getWallet(request.destinationWalletId());
        return mapper.toResponse(transaction, null, sourceWalletResponse, destinationWalletResponse);
    }

    // check cateId và walletId có phải thuộc về transactionId này hay ko
    @Override
    @Transactional
    public TransactionResponse updateTransaction(Long id, TransactionRequest request) {
        Long userId = SecurityContext.getCurrentUserId();

        Transaction oldTransaction = transactionRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new AppException(ErrorCode.TRANSACTION_NOT_FOUND));

        // k cần check tồn tại ví, vì nếu ví k tồn tại (đã bị xóa từ trước) thì các giao dịch cũng đã bị xóa
        // hoàn trả lại số dư của giao dịch cũ
        var undoBalanceRequest = new UpdateBalanceRequest(
                oldTransaction.getWalletId(),
                oldTransaction.getType(),
                oldTransaction.getAmount()
        );
        walletClient.updateBalanceByTransactionType(undoBalanceRequest, true);

        // update mapper
        mapper.update(oldTransaction, request);
        oldTransaction.setCategoryId(request.categoryId());
        oldTransaction.setWalletId(request.walletId());
        transactionRepository.save(oldTransaction);

        // update balance in wallet
        var updateBalanceRequest = new UpdateBalanceRequest(request.walletId(), request.type(), request.amount());
        walletClient.updateBalanceByTransactionType(updateBalanceRequest, false);

        // map to response
        CategoryResponse categoryResponse = categoryClient.getCategoryById(request.categoryId());
        WalletResponse walletResponse = walletClient.getWallet(request.walletId());
        return mapper.toResponse(oldTransaction, categoryResponse, walletResponse, null);
    }

    @Override
    @Transactional
    public void deleteTransaction(Long id) {
        Long userId = SecurityContext.getCurrentUserId();

        Transaction transaction = transactionRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new AppException(ErrorCode.TRANSACTION_NOT_FOUND));

        // update balance in wallet
        var updateBalanceRequest = new UpdateBalanceRequest(transaction.getWalletId(), transaction.getType(), transaction.getAmount());
        walletClient.updateBalanceByTransactionType(updateBalanceRequest, true);

        transactionRepository.deleteByIdAndUserId(id, userId);
    }
}
