package com.tanpuh.wallet.service;

import com.tanpuh.wallet.dto.WalletResponse;
import com.tanpuh.common.exception.AppException;
import com.tanpuh.common.exception.ErrorCode;
import com.tanpuh.common.util.SecurityContext;
import com.tanpuh.wallet.dto.CreateWalletRequest;
import com.tanpuh.wallet.dto.UpdateWalletRequest;
import com.tanpuh.wallet.entity.Wallet;
import com.tanpuh.wallet.mapper.WalletMapper;
import com.tanpuh.wallet.repo.WalletRepository;
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
}
