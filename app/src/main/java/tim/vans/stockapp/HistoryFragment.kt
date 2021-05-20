package tim.vans.stockapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import tim.vans.stockapp.data.historyStock.HistoryStock
import tim.vans.stockapp.data.historyStock.HistoryStockDao
import tim.vans.stockapp.data.historyStock.HistoryStockDatabase
import kotlin.math.round

class HistoryFragment: Fragment() {
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view:View = inflater.inflate(R.layout.fragment_history, container, false)
        recyclerView = view.findViewById<View>(R.id.recyclerView) as RecyclerView

        val historyStockDB = HistoryStockDatabase.getDatabase(view.context)
        val historyStockDao = historyStockDB.historyStockDao()


        val historyStocksList :List<HistoryStock> = historyStockDao.getAllData()

        updateTotal(view, historyStocksList)

        val myAdapter = HistoryStockAdapter(historyStocksList, view.context)
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        recyclerView.adapter = myAdapter

        val fabButton = view.findViewById<View>(R.id.fab)
        val cancelButton = view.findViewById<View>(R.id.cancel_button)
        val addButton = view.findViewById<View>(R.id.addButton)


        showHideForm(view, false)
        fabButton.setOnClickListener {
            showHideForm(view, true)
        }
        cancelButton.setOnClickListener {
            showHideForm(view, false)
            clearInputs(view)
        }

        addButton.setOnClickListener {
            submitForm(view, historyStockDao)
        }

        return view
    }

    private fun clearInputs(view: View){
        val inputName: TextInputLayout = view.findViewById(R.id.textInputName)
        inputName.editText?.text?.clear()
        val inputAmount: TextInputLayout = view.findViewById(R.id.textInputAmount)
        inputAmount.editText?.text?.clear()
        val inputPriceBought: TextInputLayout = view.findViewById(R.id.textInputPriceBought)
        inputPriceBought.editText?.text?.clear()
        val inputPriceSold: TextInputLayout = view.findViewById(R.id.textInputPriceSold)
        inputPriceSold.editText?.text?.clear()
    }

    private fun submitForm(
        view: View,
        historyStockDao: HistoryStockDao
    ) {
        val inputName: TextInputLayout = view.findViewById(R.id.textInputName)
        val name = inputName.editText?.text.toString()
        val inputAmount: TextInputLayout = view.findViewById(R.id.textInputAmount)
        val amount = inputAmount.editText?.text.toString()
        val inputPriceBought: TextInputLayout = view.findViewById(R.id.textInputPriceBought)
        val boughtPrice = inputPriceBought.editText?.text.toString()
        val inputPriceSold: TextInputLayout = view.findViewById(R.id.textInputPriceSold)
        val soldPrice = inputPriceSold.editText?.text.toString()

        if (name.isEmpty() or amount.isEmpty() or boughtPrice.isEmpty() or soldPrice.isEmpty()){
            Toast.makeText(view.context, "Please make sure all fields are entered" , Toast.LENGTH_LONG).show()
            return
        } else if ((amount.toInt() < 0) or (boughtPrice.toDouble() <= 0) or (soldPrice.toDouble() <= 0)){
            Toast.makeText(view.context, "Numbers can not be zero or negative" , Toast.LENGTH_LONG).show()
            return
        } else{
            val newHistoryStock = HistoryStock(0,name,amount.toInt(), boughtPrice.toDouble(), amount.toInt(), soldPrice.toDouble())
            addStock(newHistoryStock, view, historyStockDao)
            clearInputs(view)
            showHideForm(view, false)
            return
        }

    }

    private fun addStock(
        historyStock: HistoryStock,
        view: View,
        historyStockDao: HistoryStockDao
    ){
        historyStockDao.addOrReplaceStock(historyStock)
        val historyStocksList = historyStockDao.getAllData()
        updateTotal(view, historyStocksList)
        val myAdapter = HistoryStockAdapter(historyStocksList, view.context)
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        recyclerView.adapter = myAdapter
    }

    private fun updateTotal(view: View, historyStocksList: List<HistoryStock>){
        var totalStat = 0.0
        var totalStocks = 0
        for (historyStock in historyStocksList) {
            totalStat += historyStock.statMoney
            totalStocks += historyStock.soldAmount
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

    private fun showHideForm(view: View, show: Boolean)  {
        val fabButton = view.findViewById<View>(R.id.fab)
        val overlay = view.findViewById<View>(R.id.overlay)
        val form = view.findViewById<View>(R.id.form)

        if (show){
            overlay.visibility = View.VISIBLE
            form.visibility = View.VISIBLE
            fabButton.visibility = View.INVISIBLE
        } else {
            overlay.visibility = View.INVISIBLE
            form.visibility = View.INVISIBLE
            fabButton.visibility = View.VISIBLE

        }
    }
}