package chillspace.chillspace.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import chillspace.chillspace.R
import chillspace.chillspace.adapters.CompletedTransactionRecyclerAdapter
import chillspace.chillspace.models.CompletedTransaction
import chillspace.chillspace.viewmodels.CompletedTransactionsViewModel
import kotlinx.android.synthetic.main.fragment_completed_transactions.*

class CompletedTransactionsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_completed_transactions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler_completedTransactions.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
    }

    override fun onResume() {
        super.onResume()

        val completedTransactionViewModel = ViewModelProviders.of(this).get(CompletedTransactionsViewModel::class.java)

        val transactionListLiveData : LiveData<ArrayList<CompletedTransaction>> = completedTransactionViewModel.getCurrentTransactionLiveData()

        transactionListLiveData.observe(this, Observer {
            recycler_completedTransactions.adapter = CompletedTransactionRecyclerAdapter(it)
        })
    }
}
