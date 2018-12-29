package chillspace.chillspace.views

import android.app.Activity
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import chillspace.chillspace.R
import chillspace.chillspace.models.GeneratedOTP
import chillspace.chillspace.viewmodels.CurrentTransactionViewModel
import chillspace.chillspace.viewmodels.OTP_List_ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_home.*
import kotlin.random.Random

class HomeFragment : Fragment() {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    var otpList = ArrayList<Int>()
    private val dbRef = FirebaseDatabase.getInstance().reference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        //allowing menu in toolbar
        setHasOptionsMenu(true)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val otpListViewModel = ViewModelProviders.of(this).get(OTP_List_ViewModel::class.java)

        val otpListLiveData = otpListViewModel.getOTPListLiveData()

        otpListLiveData.observe(this, Observer {
            if(it!=null)
                otpList = it
        })
    }

    override fun onResume() {
        super.onResume()

        val imgBtn = imgBtn_play_stop

        val currentTransactionViewModel = ViewModelProviders.of(this).get(CurrentTransactionViewModel::class.java)
        val currentTransactionLiveData = currentTransactionViewModel.getCurrentTransactionLiveData()

        currentTransactionLiveData.observe(activity as LifecycleOwner, Observer {
            if (it != null) {
                if(it.isActive!!){
                    imgBtn.setImageResource(R.drawable.ic_stop_black_24dp)
                    imgBtn.setOnClickListener(onStopCLickedListener)
                }
                else{
                    imgBtn.setImageResource(R.drawable.ic_play_circle_outline_black_24dp)
                    imgBtn.setOnClickListener(onStartCLickedListener)
                }
            }else{
                imgBtn.setImageResource(R.drawable.ic_play_circle_outline_black_24dp)
                imgBtn.setOnClickListener(onStartCLickedListener)
            }
        })
    }

    private val onStartCLickedListener = View.OnClickListener {
        Toast.makeText(activity, "Starting", Toast.LENGTH_SHORT).show()

        val otp = generateOTP()
        otpList.add(otp)
        dbRef.child("OTP_List").setValue(otpList)
        dbRef.child("GeneratedOTPs").child(otp.toString()).setValue(GeneratedOTP(firebaseAuth.currentUser?.uid.toString(),false))

        txt_OTP.text = otp.toString()
    }

    private val onStopCLickedListener = View.OnClickListener {
        Toast.makeText(activity, "Ending", Toast.LENGTH_SHORT).show()
    }

    private fun generateOTP() : Int{
        var otp = Random.nextInt(1111,9999)

        while (otpList.contains(otp)){
            otp = Random.nextInt(1111,9999)
        }

        return otp
    }

    //creating menu in the toolbar
    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_toolbar_home, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    //NOTE : You may use NavigationUI if you want to navigate always to the frag with same id as menu id
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when {
            item!!.itemId == R.id.logout_home -> firebaseAuth.signOut()
            item.itemId == R.id.dest_profile -> Navigation.findNavController(activity as Activity, R.id.nav_host_fragment).navigate(R.id.dest_profile)
        }
        return super.onOptionsItemSelected(item)
    }
}
