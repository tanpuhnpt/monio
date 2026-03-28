package com.mpt.monio.wallet.service;

import com.mpt.monio.wallet.dto.CreateWalletRequest;
import com.mpt.monio.wallet.dto.UpdateWalletRequest;
import com.mpt.monio.wallet.dto.WalletResponse;

import java.util.List;

public interface WalletService {
    List<WalletResponse> getAllWallets();

    WalletResponse getWallet(Long id);

    WalletResponse createWallet(CreateWalletRequest createWalletRequest);

    Object updateWallet(Long id, UpdateWalletRequest updateWalletRequest);

    void deleteWallet(Long id);
}
