package com.tanpuh.wallet.controller;

import com.tanpuh.wallet.dto.CreateWalletRequest;
import com.tanpuh.wallet.dto.UpdateWalletRequest;
import com.tanpuh.wallet.service.WalletService;
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

    @GetMapping("/{id}")
    @Operation(summary = "Show a wallet for current user")
    public ResponseEntity<?> getWallet(@PathVariable Long id) {
        return ResponseEntity.ok(walletService.getWallet(id));
    }

    @PostMapping
    @Operation(summary = "Create a new wallet")
    public ResponseEntity<?> createWallet(@RequestBody @Valid CreateWalletRequest createWalletRequest) {
        return ResponseEntity.ok(walletService.createWallet(createWalletRequest));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a wallet info")
    public ResponseEntity<?> updateWallet(@PathVariable Long id, @RequestBody @Valid UpdateWalletRequest updateWalletRequest) {
        return ResponseEntity.ok(walletService.updateWalletInfo(id, updateWalletRequest));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a wallet")
    public ResponseEntity<?> deleteWallet(@PathVariable Long id) {
        walletService.deleteWallet(id);
        return ResponseEntity.ok().build();
    }
}
