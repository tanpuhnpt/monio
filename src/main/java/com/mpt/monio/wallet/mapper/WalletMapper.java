package com.mpt.monio.wallet.mapper;

import com.mpt.monio.wallet.dto.CreateWalletRequest;
import com.mpt.monio.wallet.dto.WalletResponse;
import com.mpt.monio.wallet.entity.Wallet;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WalletMapper {
    Wallet toEntity(CreateWalletRequest request);
    WalletResponse toResponse(Wallet entity);
}
