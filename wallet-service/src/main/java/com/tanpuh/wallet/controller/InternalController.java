package com.tanpuh.wallet.controller;

import com.tanpuh.wallet.dto.TransferRequest;
import com.tanpuh.wallet.dto.UpdateBalanceRequest;
import com.tanpuh.wallet.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wallets")
@RequiredArgsConstructor
public class InternalController {
    private final WalletService walletService;

    @GetMapping("/{id}/exists")
    @Operation(summary = "Show a wallet for current user")
    public ResponseEntity<?> existsById(@PathVariable Long id) {
        return ResponseEntity.ok(walletService.existsById(id));
    }

    @GetMapping("/list")
    @Operation(summary = "Show all wallets by ids for current user")
    public ResponseEntity<?> getAllWalletsByIds(@RequestParam("ids") List<Long> ids) {
        return ResponseEntity.ok(walletService.getAllWalletsByIds(ids));
    }

    @PutMapping("/update-balance")
    @Operation(summary = "Update a wallet balance")
    public ResponseEntity<?> updateBalance(@RequestBody @Valid UpdateBalanceRequest request,
                                           @RequestParam(defaultValue = "false") boolean isReverse) {
        walletService.updateBalanceByTransactionType(request, isReverse);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/transfer")
    @Operation(summary = "Update balances between 2 wallets")
    public ResponseEntity<?> updateWallet(@RequestBody @Valid TransferRequest request) {
        walletService.updateBalances(request);
        return ResponseEntity.ok().build();
    }
}
