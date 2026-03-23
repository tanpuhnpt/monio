package com.mpt.monio.wallet.controller;

import com.mpt.monio.wallet.dto.WalletRequest;
import com.mpt.monio.wallet.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wallets")
@RequiredArgsConstructor
public class WalletController {
    private final WalletService walletService;

    @GetMapping
    @Operation(summary = "Show all wallets for current user")
    public ResponseEntity<?> getAllWallets() {
        return ResponseEntity.ok(walletService.getAllWallets());
    }

    @PostMapping
    @Operation(summary = "Create a new wallet")
    public ResponseEntity<?> createWallet(@RequestBody @Valid WalletRequest walletRequest) {
        return ResponseEntity.ok(walletService.createWallet(walletRequest));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a wallet")
    public ResponseEntity<?> updateWallet(@PathVariable Long id, @RequestBody WalletRequest walletRequest) {
        return ResponseEntity.ok(walletService.updateWallet(id, walletRequest));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a wallet")
    public ResponseEntity<?> deleteWallet(@PathVariable Long id) {
        walletService.deleteWallet(id);
        return ResponseEntity.ok().build();
    }
}
