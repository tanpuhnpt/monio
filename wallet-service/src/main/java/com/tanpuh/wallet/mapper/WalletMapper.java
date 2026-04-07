package com.tanpuh.wallet.mapper;

import com.tanpuh.wallet.dto.WalletResponse;
import com.tanpuh.wallet.dto.CreateWalletRequest;
import com.tanpuh.wallet.entity.Wallet;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WalletMapper {
    Wallet toEntity(CreateWalletRequest request);
    WalletResponse toResponse(Wallet entity);
}
