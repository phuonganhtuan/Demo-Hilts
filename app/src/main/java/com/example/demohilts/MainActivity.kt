package com.example.demohilts

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.demohilts.data.entity.Coin
import com.example.demohilts.data.service.CoroutineState
import com.example.demohilts.databinding.ActivityMainBinding
import com.example.demohilts.utils.gone
import com.example.demohilts.utils.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mainViewModel.getBTCPrice()
        observeData()
        setupEvents()
    }

    private fun observeData() {
        lifecycleScope.launchWhenCreated {
            mainViewModel.btcPrice.collect {
                when (it.status) {
                    CoroutineState.LOADING -> displayLoadingState()
                    CoroutineState.SUCCESS -> {
                        it.data?.let { coin -> displaySuccessState(coin) }
                    }
                    CoroutineState.ERROR -> displayErrorState(it.message ?: "Error!")
                }
            }
        }
    }

    private fun setupEvents() = with(binding) {
        layoutRefresh.setOnRefreshListener {
            mainViewModel.getBTCPrice()
        }
    }

    private fun displayLoadingState() = with(binding) {
        viewBg.show()
    }

    private fun displayErrorState(message: String) = with(binding) {
        viewBg.show()
        layoutRefresh.isRefreshing = false
        Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
    }

    private fun displaySuccessState(data: Coin) = with(binding) {
        viewBg.gone()
        layoutRefresh.isRefreshing = false
        textCoinName.text = data.chartName
        textPriceUSD.text = data.usdPrice
        textPriceGBP.text = data.gbpPrice
        textPriceEUR.text = data.eurPrice
    }
}
