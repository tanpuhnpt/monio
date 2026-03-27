package com.mpt.monio.transaction.mapper;

import com.mpt.monio.category.mapper.CategoryMapper;
import com.mpt.monio.transaction.dto.TransactionRequest;
import com.mpt.monio.transaction.dto.TransactionResponse;
import com.mpt.monio.transaction.entity.Transaction;
import com.mpt.monio.wallet.mapper.WalletMapper;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class, WalletMapper.class})
public interface TransactionMapper {
    Transaction toEntity(TransactionRequest request);

    TransactionResponse toResponse(Transaction entity);

    void update(@MappingTarget Transaction entity, TransactionRequest request);
}
