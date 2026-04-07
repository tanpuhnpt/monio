package com.tanpuh.wallet.service;

import com.tanpuh.wallet.dto.WalletResponse;
import com.tanpuh.wallet.dto.CreateWalletRequest;
import com.tanpuh.wallet.dto.UpdateWalletRequest;

import java.util.List;

public interface WalletService {
    List<WalletResponse> getAllWallets();

    WalletResponse getWallet(Long id);

    WalletResponse createWallet(CreateWalletRequest createWalletRequest);

    WalletResponse updateWalletInfo(Long id, UpdateWalletRequest updateWalletRequest);

    void deleteWallet(Long id);
}
