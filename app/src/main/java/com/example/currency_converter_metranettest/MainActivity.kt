package com.example.currency_converter_metranettest

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap


class MainActivity : AppCompatActivity() {
    private var requestQueue: RequestQueue? = null
    private var currencySymbolsMap = mutableMapOf<String, Any>()

    private var symbolsResponse: JSONObject? = null
    private var ratesResponse: JSONObject? = null
    private val currencyRatesAndSymbols: HashMap<String, Any> = hashMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestQueue = Volley.newRequestQueue(this)
        setContentView(R.layout.activity_main)
        et_amount.setText("0")
        getCurrencySymbols()
        getCurrencyRates()
        btnConvert.setOnClickListener {
            calculate()
        }
    }

    private fun calculate() {
        if (et_amount.text.toString() == "0") {
            Toast.makeText(
                this,
                "Silahkan masukan jumlah mata uang yang ingin di konversi",
                Toast.LENGTH_SHORT
            ).show()
        }
        else {
            val namesAndValuesMap = matchCurrencyRatesWithSymbol(ratesResponse, symbolsResponse)
            val amountConverted = calculateAmount(namesAndValuesMap[from_spinner.selectedItem.toString()]!!,
                namesAndValuesMap[to_spinner.selectedItem.toString()]!!,
                et_amount.text!!)
            tv_totalConverted.text = amountConverted
        }
    }

    private fun setSpinnerSymbols() {
        val currencySymbols = ArrayList(currencySymbolsMap.values)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencySymbols)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        from_spinner.adapter = adapter
        to_spinner.adapter = adapter
        from_spinner.setTitle("From")
        to_spinner.setTitle("To")

        from_spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                tv_fromcurrency.text = from_spinner.selectedItem.toString()
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // your code here
            }
        }

        to_spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                tv_tocurrency.text = to_spinner.selectedItem.toString()
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // your code here
            }
        }
    }

    private fun getCurrencySymbols(): MutableMap<String, Any> {
        val url = "http://api.exchangeratesapi.io/v1/symbols?access_key=c91ce2181b340fb10802feb7eb7ff620"
        Log.e("value symbols ", "lalala")
        val request = JsonObjectRequest(Request.Method.GET, url, null, { response ->
            try {
                symbolsResponse = response.getJSONObject("symbols")
                val jsonObjectSymbols = response.getJSONObject("symbols")
                for (i in 0 until jsonObjectSymbols.length()) {
                    val key = jsonObjectSymbols.names().getString(i)
                    val valueKey = jsonObjectSymbols.get(key)
                    currencySymbolsMap[key] = valueKey
                }
                Log.e("value symbolsMap ", currencySymbolsMap.toString())
                setSpinnerSymbols()
            } catch (e: JSONException) {
                Log.e("Error", "$e");
                e.printStackTrace()
            }
        }, { error ->
            Log.e("HAHAHA", "hehehe")
            error.printStackTrace()
        })
        requestQueue?.add(request)
        return currencySymbolsMap
    }

    private fun getCurrencyRates(): MutableMap<String, Any> {
        val url = "http://api.exchangeratesapi.io/v1/latest?access_key=c91ce2181b340fb10802feb7eb7ff620"
        val request = JsonObjectRequest(Request.Method.GET, url, null, { response ->
            try {
                ratesResponse = response.getJSONObject("rates")
            } catch (e: JSONException) {
                Log.e("Error", "$e");
                e.printStackTrace()
            }
        }, { error ->
            error.printStackTrace()
        })
        requestQueue?.add(request)
        return currencySymbolsMap
    }

    private fun matchCurrencyRatesWithSymbol(rates: JSONObject?, symbol: JSONObject?): HashMap<String, Any>{
        for (i in 0 until rates!!.length()) {
            val keyRates = rates.names().getString(i)
            val valueRates = rates.get(keyRates)
            val keySymbol = symbol!!.names().getString(i)
            val valueSymbol = symbol.get(keySymbol).toString()
            if(keyRates == keySymbol){ currencyRatesAndSymbols[valueSymbol] = valueRates }
        }
        return currencyRatesAndSymbols
    }

    private fun calculateAmount(currency1: Any, currency2: Any, amount: Any): String =
        (amount.toString().toDouble() * currency2.toString().toDouble() / currency1.toString().toDouble()).toString()
}


