package com.mpt.monio.transaction.controller;

import com.mpt.monio.transaction.dto.TransactionRequest;
import com.mpt.monio.transaction.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService service;

    @GetMapping
    @Operation(summary = "Show all transactions for current user with date range filter")
    public ResponseEntity<?> getAllTransactions(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        return ResponseEntity.ok(service.getAllTransactions(startDate, endDate));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Show a transaction for current user")
    public ResponseEntity<?> getTransaction(@PathVariable Long id) {
        return ResponseEntity.ok(service.getTransaction(id));
    }

    @PostMapping
    @Operation(summary = "Create a new transaction")
    public ResponseEntity<?> createTransaction(@RequestBody @Valid TransactionRequest request) {
        return ResponseEntity.ok(service.createTransaction(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a transaction")
    public ResponseEntity<?> updateTransaction(@PathVariable Long id, @RequestBody @Valid TransactionRequest request) {
        return ResponseEntity.ok(service.updateTransaction(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a transaction")
    public ResponseEntity<?> deleteTransaction(@PathVariable Long id) {
        service.deleteTransaction(id);
        return ResponseEntity.ok().build();
    }
}
