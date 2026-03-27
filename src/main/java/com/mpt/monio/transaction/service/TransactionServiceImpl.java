package com.mpt.monio.transaction.service;

import com.mpt.monio.auth.entity.User;
import com.mpt.monio.auth.repo.UserRepository;
import com.mpt.monio.category.entity.Category;
import com.mpt.monio.category.repo.CategoryRepository;
import com.mpt.monio.exception.AppException;
import com.mpt.monio.exception.ErrorCode;
import com.mpt.monio.transaction.dto.TransactionRequest;
import com.mpt.monio.transaction.dto.TransactionResponse;
import com.mpt.monio.transaction.entity.Transaction;
import com.mpt.monio.transaction.entity.TransactionType;
import com.mpt.monio.transaction.mapper.TransactionMapper;
import com.mpt.monio.transaction.repo.TransactionRepository;
import com.mpt.monio.wallet.entity.Wallet;
import com.mpt.monio.wallet.repo.WalletRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class TransactionServiceImpl implements TransactionService {
    UserRepository userRepository;
    CategoryRepository categoryRepository;
    WalletRepository walletRepository;
    TransactionMapper mapper;
    TransactionRepository transactionRepository;

    @Override
    public List<TransactionResponse> getAllTransactions(LocalDate startDate, LocalDate endDate) {
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");

        // mặc định nếu không truyền thì lấy từ đầu tháng đến hiện tại
        LocalDateTime startDateTime = (startDate != null)
                ? startDate.atStartOfDay()
                : LocalDate.now().withDayOfMonth(1).atStartOfDay();

        LocalDateTime endDateTime = (endDate != null)
                ? endDate.atTime(LocalTime.MAX)
                : LocalDateTime.now();

        return transactionRepository
                .findAllByUserId(userId, startDateTime, endDateTime, sort)
                .stream()
                .map(mapper::toResponse)
                .toList();
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

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        Wallet wallet = walletRepository.findByIdAndUserIdAndIsActiveTrue(request.getWalletId(), userId)
                .orElseThrow(() -> new AppException(ErrorCode.WALLET_NOT_FOUND));

        Transaction transaction = mapper.toEntity(request);
        transaction.setUser(user);
        transaction.setCategory(category);
        transaction.setWallet(wallet);

        // update balance in wallet
        if (request.getType() == TransactionType.EXPENSE)
            wallet.setBalance(wallet.getBalance().subtract(request.getAmount()));
        else
            wallet.setBalance(wallet.getBalance().add(request.getAmount()));

        walletRepository.save(wallet);

        return mapper.toResponse(transactionRepository.save(transaction));
    }

    @Override
    @Transactional
    public TransactionResponse updateTransaction(Long id, TransactionRequest request) {
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());

        Transaction transaction = transactionRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new AppException(ErrorCode.TRANSACTION_NOT_FOUND));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        Wallet wallet = walletRepository.findByIdAndUserIdAndIsActiveTrue(request.getWalletId(), userId)
                .orElseThrow(() -> new AppException(ErrorCode.WALLET_NOT_FOUND));

        // update mapper
        mapper.update(transaction, request);
        transaction.setCategory(category);
        transaction.setWallet(wallet);

        // update balance in wallet
        if (request.getType() == TransactionType.EXPENSE)
            wallet.setBalance(wallet.getBalance().subtract(request.getAmount()));
        else
            wallet.setBalance(wallet.getBalance().add(request.getAmount()));

        walletRepository.save(wallet);

        return mapper.toResponse(transactionRepository.save(transaction));
    }

    @Override
    @Transactional
    public void deleteTransaction(Long id) {
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());

        Transaction transaction = transactionRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new AppException(ErrorCode.TRANSACTION_NOT_FOUND));

        // update balance in wallet
        Wallet wallet = transaction.getWallet();
        if (transaction.getType() == TransactionType.EXPENSE)
            wallet.setBalance(wallet.getBalance().add(transaction.getAmount()));
        else
            wallet.setBalance(wallet.getBalance().subtract(transaction.getAmount()));
        walletRepository.save(wallet);

        transactionRepository.deleteByIdAndUserId(id, userId);
    }
}
