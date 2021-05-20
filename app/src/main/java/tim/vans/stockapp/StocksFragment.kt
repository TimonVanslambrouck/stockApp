package tim.vans.stockapp

import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import okhttp3.*
import tim.vans.stockapp.data.historyStock.HistoryStock
import tim.vans.stockapp.data.historyStock.HistoryStockDatabase
import tim.vans.stockapp.data.stock.Stock
import tim.vans.stockapp.data.stock.StockDao
import tim.vans.stockapp.data.stock.StockDatabase
import java.io.IOException
import kotlin.concurrent.fixedRateTimer
import kotlin.math.round


class StocksFragment: Fragment() {
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view:View = inflater.inflate(R.layout.fragment_stocks, container, false)
        recyclerView = view.findViewById<View>(R.id.recyclerView) as RecyclerView

        val stockDB = StockDatabase.getDatabase(view.context)
        val stockDao = stockDB.stockDao()

        val stocksList :List<Stock> = stockDao.getAllData()

        updateTotal(stocksList,view)

        val fabButton = view.findViewById<View>(R.id.fab)
        val cancelButton = view.findViewById<View>(R.id.cancel_button)
        val cancelButton2 = view.findViewById<View>(R.id.cancel_button2)
        val addButton = view.findViewById<View>(R.id.addButton)
        val addButton2 = view.findViewById<View>(R.id.addButton2)
        val form = view.findViewById<View>(R.id.form)
        val form2 = view.findViewById<View>(R.id.form2)
        val idTextView = view.findViewById<TextView>(R.id.idText)

        val myAdapter = StockAdapter(stocksList, view.context,
            form2 as ConstraintLayout, view.findViewById(R.id.overlay), fabButton, idTextView)
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        recyclerView.adapter = myAdapter

        val items = listOf( "AAPL", "AMZN", "BABA", "FB", "GOOG", "GOOGL","IBM","MSFT", "PYPL", "TSLA")

        // Source: https://www.geeksforgeeks.org/autocompletetextview-in-kotlin/
        // Source: https://material.io/components/menus/android#exposed-dropdown-menus
        val arrayAdapter = ArrayAdapter(view.context, R.layout.list_item, items)
        val textView = view.findViewById<View>(R.id.formAutoComplete) as AutoCompleteTextView
        textView.setAdapter(arrayAdapter)

        showHideForm(view, false, form)
        fabButton.setOnClickListener {
            showHideForm(view, true, form)
        }
        cancelButton.setOnClickListener {
            showHideForm(view, false, form)
            clearInputs(view)
        }

        addButton.setOnClickListener {
            submitForm(view, stockDao)
        }
        cancelButton2.setOnClickListener {
            showHideForm(view, false, form2)
        }
        addButton2.setOnClickListener {
            sellStock(view, stockDao)
        }

        // Source: https://stackoverflow.com/a/55571042
        fixedRateTimer("updateTimer", false, 0L, 60 * 1000 * 5) {
            activity?.runOnUiThread {
                updateCurrentPrice(stocksList,view,stockDao)
                updateTotal(stocksList,view)
            }
        }

        return view
    }

    private fun sellStock(view: View, stockDao: StockDao) {
        val id = view.findViewById<TextView>(R.id.idText)
        val idString = id.text.toString()
        val idNumber = idString.toInt()
        val stock = stockDao.getStockByID(idNumber)
        val inputAmount: TextInputLayout = view.findViewById(R.id.textInputAmount2)
        val amount = inputAmount.editText?.text.toString().toInt()
        val inputPrice: TextInputLayout = view.findViewById(R.id.textInputPrice2)
        val price = inputPrice.editText?.text.toString().toDouble()

        if (stock.boughtAmount < amount){
            Toast.makeText(view.context, "You only have ${stock.boughtAmount} stocks" , Toast.LENGTH_LONG).show()
        } else if (price <= 0){
            Toast.makeText(view.context, "Price can't be negative or zero" , Toast.LENGTH_LONG).show()
        } else{
            if (stock.boughtAmount == amount) {
                stockDao.deleteStock(stock)
            } else {
                stock.boughtAmount -= amount
                stock.updateStats()
                stockDao.addOrReplaceStock(stock)
            }
            val newHistoryStock = HistoryStock(0,stock.nameStock,stock.boughtAmount,stock.boughtPrice,amount,price)
            val historyStockDB = HistoryStockDatabase.getDatabase(view.context)
            val historyStockDao = historyStockDB.historyStockDao()
            historyStockDao.addOrReplaceStock(newHistoryStock)
            showHideForm(view,false,view.findViewById(R.id.form2))
            val stockList = stockDao.getAllData()
            updateTotal(stockList, view)
            val myAdapter = StockAdapter(stockList, view.context, view.findViewById(R.id.form2), view.findViewById(R.id.overlay), view.findViewById(R.id.fab), id)
            recyclerView.layoutManager = LinearLayoutManager(view.context)
            recyclerView.adapter = myAdapter
        }

    }

    private fun updateTotal(stocksList: List<Stock>, view: View) {
        var totalStat = 0.0
        var totalStocks = 0
        for (stock in stocksList) {
            totalStat += stock.statMoney
            totalStocks += stock.boughtAmount
        }
        totalStat *= 100
        totalStat = round(totalStat)
        totalStat /= 100

        val totalStocksView = view.findViewById<View>(R.id.totalStocks) as TextView
        totalStocksView.text =("$totalStocks stocks")

        val totalStatView = view.findViewById<View>(R.id.totalStat) as TextView
        if (totalStat > 0){
            totalStatView.text = ("+$totalStat€")
        } else {
            totalStatView.text = ("$totalStat€")
        }
    }

    private fun submitForm(view: View, stockDao: StockDao) {
        val inputName: TextInputLayout = view.findViewById(R.id.textInputName)
        val name = inputName.editText?.text.toString()
        val inputAmount: TextInputLayout = view.findViewById(R.id.textInputAmount)
        val amount = inputAmount.editText?.text.toString()
        val inputPrice: TextInputLayout = view.findViewById(R.id.textInputPrice)
        val price = inputPrice.editText?.text.toString()

        if (name.isEmpty() or amount.isEmpty() or price.isEmpty()){
            Toast.makeText(view.context, "Please make sure all fields are entered" , Toast.LENGTH_LONG).show()
            return
        } else if ((amount.toInt() <= 0) or (price.toDouble() <= 0)){
            Toast.makeText(view.context, "Numbers can not be zero or negative" , Toast.LENGTH_LONG).show()

        } else{
            val policy = ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
            fetchStockData(view, name, stockDao,amount.toInt(),price.toDouble())

        }
    }

    private fun addStock(
        stock: Stock,
        view: View,
        stockDao: StockDao
    ){
        stockDao.addOrReplaceStock(stock)
        val stockList = stockDao.getAllData()
        updateTotal(stockList, view)
        val myAdapter = StockAdapter(stockList, view.context, view.findViewById(R.id.form2), view.findViewById(R.id.overlay), view.findViewById(R.id.fab), view.findViewById(R.id.idText))
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        recyclerView.adapter = myAdapter
    }

    private fun clearInputs(view: View){
        val inputName: TextInputLayout = view.findViewById(R.id.textInputName)
        inputName.editText?.text?.clear()
        val inputAmount: TextInputLayout = view.findViewById(R.id.textInputAmount)
        inputAmount.editText?.text?.clear()
        val inputPrice: TextInputLayout = view.findViewById(R.id.textInputPrice)
        inputPrice.editText?.text?.clear()
    }

    private fun showHideForm(view: View, show: Boolean, form: View)  {
        val button = view.findViewById<View>(R.id.fab)
        val overlay = view.findViewById<View>(R.id.overlay)

        if (show){
            overlay.visibility = View.VISIBLE
            form.visibility = View.VISIBLE
            button.visibility = View.INVISIBLE
        } else {
            overlay.visibility = View.INVISIBLE
            form.visibility = View.INVISIBLE
            button.visibility = View.VISIBLE

        }
    }

    private fun fetchStockData(
        view: View,
        name: String,
        stockDao: StockDao,
        amount: Int,
        price: Double
    ){
        // Instantiate the RequestQueue
        // Source: https://developer.android.com/training/volley/simple
        // Documentation: https://www.alphavantage.co/documentation/

        val apiKey = "7B1TCYNSM6D0TJ4P"

        val url = "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol=$name&interval=5min&apikey=$apiKey"

        val request = Request.Builder()
            .url(url)
            .build()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object: Callback{
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body()?.string()
                val gson = GsonBuilder().create()
                val stock = gson.fromJson(body, JsonElement::class.java)
                val jsonObj: JsonObject = stock.asJsonObject
                val metaData = jsonObj.getAsJsonObject("Meta Data")
                val lastUpdate = metaData.getAsJsonPrimitive("3. Last Refreshed").asString
                val timeSeries = jsonObj.getAsJsonObject("Time Series (5min)")
                val latestInfo = timeSeries.getAsJsonObject(lastUpdate)
                val closeInfo = latestInfo.getAsJsonPrimitive("4. close").asString
                val newStock = Stock(0,name,amount,price, closeInfo.toDouble())
                // Source: https://stackoverflow.com/a/63502757
                activity?.runOnUiThread {
                    addStock(newStock,view,stockDao)
                    showHideForm(view, false, view.findViewById(R.id.form))
                    clearInputs(view)
                }
            }

        })
    }

    private fun updateCurrentPrice(list: List<Stock>, view: View, stockDao: StockDao){
        for (listStock in list){
            val apiKey = "7B1TCYNSM6D0TJ4P"

            val url = "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol=${listStock.nameStock}&interval=5min&apikey=$apiKey"

            val request = Request.Builder()
                .url(url)
                .build()

            val client = OkHttpClient()

            client.newCall(request).enqueue(object: Callback{
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    val body = response.body()?.string()
                    val gson = GsonBuilder().create()
                    val stock = gson.fromJson(body, JsonElement::class.java)
                    val jsonObj: JsonObject = stock.asJsonObject
                    val metaData = jsonObj.getAsJsonObject("Meta Data")
                    val lastUpdate = metaData.getAsJsonPrimitive("3. Last Refreshed").asString
                    val timeSeries = jsonObj.getAsJsonObject("Time Series (5min)")
                    val latestInfo = timeSeries.getAsJsonObject(lastUpdate)
                    val closeInfo = latestInfo.getAsJsonPrimitive("4. close").asString
                    listStock.currentPrice = closeInfo.toDouble()
                    listStock.updateStats()
                    // Source: https://stackoverflow.com/a/63502757
                    activity?.runOnUiThread {
                        addStock(listStock,view,stockDao)
                    }
                }

            })
        }
    }
}