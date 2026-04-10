package com.tanpuh.wallet.service;

import com.tanpuh.wallet.dto.*;

import java.util.List;

public interface WalletService {
    List<WalletResponse> getAllWallets();

    WalletResponse getWallet(Long id);

    WalletResponse createWallet(CreateWalletRequest createWalletRequest);

    WalletResponse updateWalletInfo(Long id, UpdateWalletRequest updateWalletRequest);

    void deleteWallet(Long id);

    Boolean existsById(Long id);

    List<WalletResponse> getAllWalletsByIds(List<Long> ids);

    void updateBalanceByTransactionType(UpdateBalanceRequest updateBalanceRequest, boolean isReverse);

    void updateBalances(TransferRequest transferRequest);
}
