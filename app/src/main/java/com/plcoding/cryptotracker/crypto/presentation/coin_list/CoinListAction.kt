package com.plcoding.cryptotracker.crypto.presentation.coin_list

import com.plcoding.cryptotracker.crypto.presentation.models.CoinUi

sealed interface CoinListAction {
    data class SelectCoin(val coin:CoinUi) : CoinListAction
    data object OnRefresh : CoinListAction
}