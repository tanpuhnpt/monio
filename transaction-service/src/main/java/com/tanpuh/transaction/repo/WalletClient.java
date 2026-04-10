package com.tanpuh.transaction.repo;

import com.tanpuh.transaction.config.RequestInterceptorImpl;
import com.tanpuh.transaction.dto.TransferRequest;
import com.tanpuh.transaction.dto.UpdateBalanceRequest;
import com.tanpuh.transaction.dto.WalletResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "wallet-service", configuration = { RequestInterceptorImpl.class })
public interface WalletClient {

    @GetMapping("/wallets/{id}/exists")
    Boolean existsById(@PathVariable Long id);

    @PutMapping("/wallets/update-balance")
    void updateBalanceByTransactionType(@RequestBody UpdateBalanceRequest updateBalanceRequest,
                                        @RequestParam(defaultValue = "false") boolean isReverse);

    @PutMapping("/wallets/transfer")
    void updateBalances(@RequestBody TransferRequest transferRequest);

    @GetMapping("/wallets/list")
    List<WalletResponse> getWalletsByIds(@RequestParam("ids") List<Long> ids);

    @GetMapping("/wallets/{id}")
    WalletResponse getWallet(@PathVariable Long id);
}
