package com.mpt.monio.wallet.service;

import com.mpt.monio.wallet.dto.WalletRequest;
import com.mpt.monio.wallet.dto.WalletResponse;

import java.util.List;

public interface WalletService {
    List<WalletResponse> getAllWallets();

    WalletResponse createWallet(WalletRequest walletRequest);

    Object updateWallet(Long id, WalletRequest walletRequest);

    void deleteWallet(Long id);
}
