package chillspace.chillspace.viewmodels

import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import chillspace.chillspace.livedata.FirebaseDatabaseLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import androidx.arch.core.util.Function
import androidx.lifecycle.LiveData
import chillspace.chillspace.models.CompletedTransaction
import chillspace.chillspace.models.CurrentTransactionClientSide

class CompletedTransactionsViewModel : ViewModel(){
    private val dbRef = FirebaseDatabase.getInstance().reference.child("CompletedTransactions").child(FirebaseAuth.getInstance().currentUser?.uid.toString())

    private val liveData = FirebaseDatabaseLiveData(dbRef as Query)

    private val completedTransactionsListLiveData = Transformations.map(liveData,Deserializer())

    private class Deserializer : Function<DataSnapshot, ArrayList<CompletedTransaction>> {
        override fun apply(dataSnapshot: DataSnapshot): ArrayList<CompletedTransaction>? {
            return dataSnapshot.toCompletedTransactionList()
        }
    }

    fun getCurrentTransactionLiveData(): LiveData<ArrayList<CompletedTransaction>> {
        return completedTransactionsListLiveData
    }
}

private fun DataSnapshot.toCompletedTransactionList(): ArrayList<CompletedTransaction>? {
    val list = ArrayList<CompletedTransaction>()
    for (snapshot in this.children) {
        val completedTransaction = snapshot.getValue(CompletedTransaction::class.java)
        list.add(completedTransaction!!)
    }
    return list
}