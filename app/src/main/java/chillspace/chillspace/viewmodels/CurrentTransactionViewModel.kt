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
import chillspace.chillspace.models.CurrentTransactionClientSide

class CurrentTransactionViewModel : ViewModel(){
    private val dbRef = FirebaseDatabase.getInstance().reference.child("Current").child("CurrentTransactions")
            .child(FirebaseAuth.getInstance().currentUser?.uid.toString())

    private val liveData = FirebaseDatabaseLiveData(dbRef as Query)

    private val currentTransactionLiveData = Transformations.map(liveData,Deserializer())

    private class Deserializer : Function<DataSnapshot, CurrentTransactionClientSide> {
        override fun apply(dataSnapshot: DataSnapshot): CurrentTransactionClientSide? {
            return dataSnapshot.getValue(CurrentTransactionClientSide::class.java)
        }
    }

    fun getCurrentTransactionLiveData(): LiveData<CurrentTransactionClientSide> {
        return currentTransactionLiveData
    }
}