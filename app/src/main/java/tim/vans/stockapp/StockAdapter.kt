package tim.vans.stockapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import tim.vans.stockapp.data.stock.Stock

// Source: Lesson example code

class StockAdapter(private val dataSet: List <Stock>,
                   private val context: Context,
                   private val form: ConstraintLayout,
                   private val overlay: View,
                   private val fabButton: View,
                   private val idTextView: TextView):

    RecyclerView.Adapter<StockAdapter.ViewHolder>(){
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameText: TextView = view.findViewById(R.id.stockName)
        val statsText: TextView = view.findViewById(R.id.stockStats)
        val statText: TextView = view.findViewById(R.id.stockStat)
        val percentText: TextView = view.findViewById(R.id.stockPercent)
        val currentPriceText: TextView = view.findViewById(R.id.stockCurrentPrice)
        val rectangle: ImageView = view.findViewById(R.id.blackRectangle)
        val leftRectangle: ImageView = view.findViewById(R.id.leftRectangle)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val  view = LayoutInflater.from(parent.context).inflate(R.layout.stock_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.nameText.text = dataSet[position].nameStock
        holder.statsText.text = getStats(position)
        holder.statText.text = getStat(position, holder)
        holder.percentText.text = getPercent(position, holder)
        holder.currentPriceText.text = getCurrentPrice(position, holder)
        holder.rectangle.setOnClickListener { updateStats(holder) }
        holder.leftRectangle.setOnClickListener { showMenu(position)}
    }

    private fun showMenu(position: Int) {
        idTextView.text = dataSet[position].id.toString()
        form.visibility = View.VISIBLE
        overlay.visibility = View.VISIBLE
        fabButton.visibility = View.INVISIBLE
    }

    private fun getCurrentPrice(position: Int, holder: ViewHolder): String {
        val priceCurrent = dataSet[position].currentPrice
        val priceBought = dataSet[position].boughtPrice

        if (priceCurrent > priceBought){
            holder.currentPriceText.setTextColor(ContextCompat.getColor(context,R.color.green))
        } else if (priceCurrent < priceBought){
            holder.currentPriceText.setTextColor(ContextCompat.getColor(context,R.color.red))
        }
        return ("$priceCurrent")
    }

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
            holder.currentPriceText.visibility = View.INVISIBLE
        } else if (holder.percentText.visibility == View.VISIBLE){
            holder.statText.visibility = View.INVISIBLE
            holder.percentText.visibility = View.INVISIBLE
            holder.currentPriceText.visibility = View.VISIBLE
        } else{
            holder.statText.visibility = View.VISIBLE
            holder.percentText.visibility = View.INVISIBLE
            holder.currentPriceText.visibility = View.INVISIBLE
        }

    }

    override fun getItemCount() = dataSet.size

    private fun getStats(position: Int): String {
        val amount = dataSet[position].boughtAmount.toString()
        val price = dataSet[position].boughtPrice
        return ("$amount stocks at $price")
    }

    private fun getStat(position:Int, holder: ViewHolder):String{
        val stat:Double = dataSet[position].statMoney
        if (stat > 0){
            holder.statText.setTextColor(ContextCompat.getColor(context,R.color.green))
            return ("+$stat")
        } else if (stat < 0) {
            holder.statText.setTextColor(ContextCompat.getColor(context,R.color.red))
        }
        return ("$stat")
    }

}