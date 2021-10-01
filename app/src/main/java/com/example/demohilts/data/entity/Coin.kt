package com.example.demohilts.data.entity

data class Coin(
    val bpi: Bpi,
    val chartName: String,
    val disclaimer: String,
    val time: Time
) {

    val usdPrice get() = bpi.USD.rate_float.toString()
        .substring(0, bpi.USD.rate_float.toString().length - 5) + " " + bpi.USD.code

    val gbpPrice get() = bpi.GBP.rate_float.toString()
        .substring(0, bpi.GBP.rate_float.toString().length - 5) + " " + bpi.GBP.code

    val eurPrice get() = bpi.EUR.rate_float.toString()
        .substring(0, bpi.EUR.rate_float.toString().length - 5) + " " + bpi.EUR.code
}
