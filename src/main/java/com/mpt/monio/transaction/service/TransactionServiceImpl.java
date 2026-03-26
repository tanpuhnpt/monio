package com.mpt.monio.transaction.service;

import com.mpt.monio.auth.entity.User;
import com.mpt.monio.auth.repo.UserRepository;
import com.mpt.monio.category.entity.Category;
import com.mpt.monio.category.repo.CategoryRepository;
import com.mpt.monio.exception.AppException;
import com.mpt.monio.exception.ErrorCode;
import com.mpt.monio.redis.RedisService;
import com.mpt.monio.transaction.dto.TransactionRequest;
import com.mpt.monio.transaction.dto.TransactionResponse;
import com.mpt.monio.transaction.entity.Transaction;
import com.mpt.monio.transaction.mapper.TransactionMapper;
import com.mpt.monio.transaction.repo.TransactionRepository;
import com.mpt.monio.wallet.entity.Wallet;
import com.mpt.monio.wallet.repo.WalletRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class TransactionServiceImpl implements TransactionService {
    RedisService redisService;
    UserRepository userRepository;
    CategoryRepository categoryRepository;
    WalletRepository walletRepository;
    TransactionMapper mapper;
    TransactionRepository transactionRepository;

    @Override
    public List<TransactionResponse> getAllTransactions() {
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());

        String key = String.format("transaction:user:%d", userId);
        List<TransactionResponse> responses = redisService.getAll(key, TransactionResponse.class);

        if (responses == null) {
            log.info("query transaction");
            responses = transactionRepository
                    .findAllByUserId(userId)
                    .stream().map(mapper::toResponse)
                    .toList();

            redisService.saveAll(key, responses);
        }

        return responses;
    }

    @Override
    public TransactionResponse getTransaction(Long id) {
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());

        Transaction transaction = transactionRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new AppException(ErrorCode.TRANSACTION_NOT_FOUND));

        return mapper.toResponse(transaction);
    }

    @Override
    @Transactional
    public TransactionResponse createTransaction(TransactionRequest request) {
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Category category = categoryRepository.findByIdAndUserIdAndIsActiveTrue(request.getCategoryId(), userId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        Wallet wallet = walletRepository.findByIdAndUserIdAndIsActiveTrue(request.getWalletId(), userId)
                .orElseThrow(() -> new AppException(ErrorCode.WALLET_NOT_FOUND));

        Transaction transaction = mapper.toEntity(request);
        transaction.setUser(user);
        transaction.setCategory(category);
        transaction.setWallet(wallet);

        wallet.setBalance(wallet.getBalance().subtract(request.getAmount()));
        walletRepository.save(wallet);

        return mapper.toResponse(transactionRepository.save(transaction));
    }

    @Override
    public TransactionResponse updateTransaction(Long id, TransactionRequest request) {
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());

        Transaction transaction = transactionRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new AppException(ErrorCode.TRANSACTION_NOT_FOUND));

        Category category = categoryRepository.findByIdAndUserIdAndIsActiveTrue(request.getCategoryId(), userId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        Wallet wallet = walletRepository.findByIdAndUserIdAndIsActiveTrue(request.getWalletId(), userId)
                .orElseThrow(() -> new AppException(ErrorCode.WALLET_NOT_FOUND));

        // update mapper
        mapper.update(transaction, request);
        transaction.setCategory(category);
        transaction.setWallet(wallet);

        return mapper.toResponse(transactionRepository.save(transaction));
    }

    @Override
    public void deleteTransaction(Long id) {
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());

        int deletedCnt = transactionRepository.deleteByIdAndUserId(id, userId);
        if (deletedCnt == 0)
            throw new AppException(ErrorCode.TRANSACTION_NOT_FOUND);

        // xử lí balance trong wallet?
    }
}
