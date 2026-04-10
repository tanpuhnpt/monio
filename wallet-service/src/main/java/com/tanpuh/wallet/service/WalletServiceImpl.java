package com.tanpuh.wallet.service;

import com.tanpuh.common.enums.TransactionType;
import com.tanpuh.wallet.dto.*;
import com.tanpuh.common.exception.AppException;
import com.tanpuh.common.exception.ErrorCode;
import com.tanpuh.common.util.SecurityContext;
import com.tanpuh.wallet.entity.Wallet;
import com.tanpuh.wallet.mapper.WalletMapper;
import com.tanpuh.wallet.repo.WalletRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class WalletServiceImpl implements WalletService {
    WalletRepository repository;
    WalletMapper mapper;

    @Override
    public List<WalletResponse> getAllWallets() {
        Long userId = SecurityContext.getCurrentUserId();

        return repository.findAllByUserIdAndIsActiveTrue(userId).stream().map(mapper::toResponse).toList();
    }

    @Override
    public WalletResponse getWallet(Long id) {
        Long userId = SecurityContext.getCurrentUserId();

        Wallet wallet = repository.findByIdAndUserIdAndIsActiveTrue(id, userId)
                .orElseThrow(() -> new AppException(ErrorCode.WALLET_NOT_FOUND));

        return mapper.toResponse(wallet);
    }

    @Override
    public WalletResponse createWallet(CreateWalletRequest createWalletRequest) {
        Long userId = SecurityContext.getCurrentUserId();

        Wallet wallet = mapper.toEntity(createWalletRequest);
        wallet.setUserId(userId);

        try {
            return mapper.toResponse(repository.save(wallet));
        } catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.WALLET_EXISTED);
        }
    }

    @Override
    public WalletResponse updateWalletInfo(Long id, UpdateWalletRequest updateWalletRequest) {
        Long userId = SecurityContext.getCurrentUserId();

        Wallet wallet = repository.findByIdAndUserIdAndIsActiveTrue(id, userId)
                .orElseThrow(() -> new AppException(ErrorCode.WALLET_NOT_FOUND));

        wallet.setName(updateWalletRequest.name());
        wallet.setCurrency(updateWalletRequest.currency());

        try {
            return mapper.toResponse(repository.save(wallet));
        } catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.WALLET_EXISTED);
        }
    }

    @Override
    public void deleteWallet(Long id) {
        Long userId = SecurityContext.getCurrentUserId();

        Wallet wallet = repository.findByIdAndUserIdAndIsActiveTrue(id, userId)
                .orElseThrow(() -> new AppException(ErrorCode.WALLET_NOT_FOUND));

        wallet.setActive(false);
        repository.save(wallet);
    }

    @Override
    public Boolean existsById(Long id) {
        return repository.existsById(id);
    }

    @Override
    @Transactional
    public void updateBalanceByTransactionType(UpdateBalanceRequest request, boolean isReverse) {
        boolean isDecrease;
        if (isReverse) // case: delete (decrease: income, increase: expense)
            isDecrease = (request.type() == TransactionType.INCOME);
        else  // case: create (decrease: expense, increase: income)
            isDecrease = (request.type() == TransactionType.EXPENSE);

        if (isDecrease)
            repository.decreaseBalance(request.id(), request.amount());
        else
            repository.increaseBalance(request.id(), request.amount());
    }

    @Override
    @Transactional
    public void updateBalances(TransferRequest request) {
        repository.decreaseBalance(request.sourceWalletId(), request.amount());
        repository.increaseBalance(request.destinationWalletId(), request.amount());
    }

    @Override
    public List<WalletResponse> getAllWalletsByIds(List<Long> ids) {
        return repository.findAllByIdIn(ids).stream().map(mapper::toResponse).toList();
    }
}
