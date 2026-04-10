package com.tanpuh.transaction.mapper;

import com.tanpuh.transaction.dto.*;
import com.tanpuh.transaction.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    Transaction toEntity(TransactionRequest request);

    Transaction toEntity(TransferTransactionRequest request);

    void update(@MappingTarget Transaction entity, TransactionRequest request);

    @Mapping(target = "id", source = "entity.id")
    TransactionResponse toResponse(Transaction entity, CategoryResponse category,
                                   WalletResponse wallet, WalletResponse destinationWallet);
}
