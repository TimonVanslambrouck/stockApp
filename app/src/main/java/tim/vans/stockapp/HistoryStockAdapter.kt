package tim.vans.stockapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import tim.vans.stockapp.data.historyStock.HistoryStock

// Source: Lesson example code

class HistoryStockAdapter(private val dataSet: List <HistoryStock>, private val context: Context):
    RecyclerView.Adapter<HistoryStockAdapter.ViewHolder>(){
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameText: TextView = view.findViewById(R.id.stockName)
        val boughtText: TextView = view.findViewById(R.id.stockStatsBought)
        val soldText: TextView = view.findViewById(R.id.stockStatsSold)
        val statText: TextView = view.findViewById(R.id.stockStat)
        val percentText: TextView = view.findViewById(R.id.stockPercent)
        val rectangle: ImageView = view.findViewById(R.id.blackRectangle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val  view = LayoutInflater.from(parent.context).inflate(R.layout.history_stock_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.nameText.text = dataSet[position].nameStock
        holder.boughtText.text = getBoughtString(position)
        holder.soldText.text = getSoldString(position)
        holder.statText.text = getStatString(position, holder)
        holder.percentText.text = getPercent(position, holder)
        holder.rectangle.setOnClickListener { updateStats(holder) }
    }

    private fun getStatString(position: Int, holder: ViewHolder): String {
        val money = dataSet[position].statMoney
        if (money > 0){
            holder.statText.setTextColor(ContextCompat.getColor(context,R.color.green))
            return ("+$money")
        } else if (money < 0){
            holder.statText.setTextColor(ContextCompat.getColor(context,R.color.red))
        }
        return ("$money")
    }

    private fun getBoughtString(position: Int): String {
    val amount = dataSet[position].boughtAmount
    val price = dataSet[position].boughtPrice
    return ("B: $amount stocks at $price")
    }

    private fun getSoldString(position: Int): String {
        val amount = dataSet[position].soldAmount
        val price = dataSet[position].soldPrice
        return ("S: $amount stocks at $price")
    }

    override fun getItemCount() = dataSet.size

    private fun getPercent(position: Int, holder: ViewHolder): String {
        val percent = dataSet[position].statPercent
        if (percent > 0){
            holder.percentText.setTextColor(ContextCompat.getColor(context,R.color.green))
            return ("+$percent%")
        } else if (percent < 0){
            holder.percentText.setTextColor(ContextCompat.getColor(context,R.color.red))
        }
        return ("$percent%")
    }

    private fun updateStats(holder: ViewHolder) {
        if (holder.statText.visibility == View.VISIBLE){
            holder.statText.visibility = View.INVISIBLE
            holder.percentText.visibility = View.VISIBLE
        } else{
            holder.statText.visibility = View.VISIBLE
            holder.percentText.visibility = View.INVISIBLE
        }

    }

}