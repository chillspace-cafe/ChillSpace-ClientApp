package chillspace.chillspace.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import chillspace.chillspace.R
import chillspace.chillspace.models.CompletedTransaction
import java.text.SimpleDateFormat
import java.util.*

class CompletedTransactionRecyclerAdapter(val listTransaction: ArrayList<CompletedTransaction>) : RecyclerView.Adapter<CompletedTransactionRecyclerAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_completed_transaction_row, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listTransaction.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val transaction = listTransaction.get(position)

        //Millisecond to DateTime
        val cal : Calendar = Calendar.getInstance()
        cal.timeInMillis = transaction.startTime!!.toLong()
        val df = SimpleDateFormat("dd/MM/yy - HH:mm:ss")
        val dateTime = df.format(cal.time)

        holder.dateTimeTextView.text = dateTime
        holder.playTimeTextView.text = transaction.elapsedTimeInMinutes.toString()+" minutes"
        holder.costTextView.text = "Rs. "+ transaction.cost.toString()
    }


    class MyViewHolder(val itemview: View) : RecyclerView.ViewHolder(itemview) {
        val dateTimeTextView = itemview.findViewById<TextView>(R.id.date_time_transaction_row)
        val playTimeTextView = itemview.findViewById<TextView>(R.id.playTime_transaction_row)
        val costTextView = itemView.findViewById<TextView>(R.id.cost_transaction_row)
    }
}