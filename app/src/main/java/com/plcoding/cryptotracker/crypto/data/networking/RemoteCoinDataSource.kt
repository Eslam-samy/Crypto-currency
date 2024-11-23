package com.plcoding.cryptotracker.crypto.data.networking

import com.plcoding.cryptotracker.core.data.mappers.toCoin
import com.plcoding.cryptotracker.core.data.mappers.toCoinPrice
import com.plcoding.cryptotracker.core.data.networking.SafeCall
import com.plcoding.cryptotracker.core.data.networking.constructUrl
import com.plcoding.cryptotracker.core.domain.util.NetworkError
import com.plcoding.cryptotracker.core.domain.util.Result
import com.plcoding.cryptotracker.core.domain.util.map
import com.plcoding.cryptotracker.crypto.data.networking.dto.CoinHistoryDto
import com.plcoding.cryptotracker.crypto.data.networking.dto.CoinsResponseDto
import com.plcoding.cryptotracker.crypto.domain.Coin
import com.plcoding.cryptotracker.crypto.domain.CoinDataSource
import com.plcoding.cryptotracker.crypto.domain.CoinPrice
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import java.time.ZoneId
import java.time.ZonedDateTime

class RemoteCoinDataSource(
    private val httpClient: HttpClient,
) : CoinDataSource {
    override suspend fun getCoins(): Result<List<Coin>, NetworkError> {
        return SafeCall<CoinsResponseDto> {
            httpClient.get(
                urlString = constructUrl(
                    url = "assets"
                )
            )
        }.map {
            it.data.map { it.toCoin() }
        }
    }

    override suspend fun getCoinHistory(
        id: String,
        start: ZonedDateTime,
        end: ZonedDateTime,
    ): Result<List<CoinPrice>, NetworkError> {
        return SafeCall<CoinHistoryDto> {
            val startMillies = start
                .withZoneSameInstant(ZoneId.of("UTC"))
                .toInstant()
                .toEpochMilli()
            val endMillies = end
                .withZoneSameInstant(ZoneId.of("UTC"))
                .toInstant()
                .toEpochMilli()
            httpClient.get(
                urlString = constructUrl(
                    url = "assets/$id/history"
                )
            ) {
                parameter("interval", "h6")
                parameter("start", startMillies)
                parameter("end", endMillies)
            }
        }.map {
            it.data.map { it.toCoinPrice() }
        }
    }
}