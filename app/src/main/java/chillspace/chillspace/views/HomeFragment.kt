package chillspace.chillspace.views

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.os.SystemClock
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import chillspace.chillspace.R
import chillspace.chillspace.interfaces.CallbackInterface
import chillspace.chillspace.models.GeneratedOTP
import chillspace.chillspace.viewmodels.CurrentTransactionViewModel
import chillspace.chillspace.viewmodels.OTP_List_ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlin.random.Random
import android.view.animation.Animation
import android.view.animation.AlphaAnimation
import android.widget.Chronometer


class HomeFragment : Fragment() {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    var otpList = ArrayList<Int>()
    private val dbRef = FirebaseDatabase.getInstance().reference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

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
            if (it != null)
                otpList = it
        })
    }

    override fun onResume() {
        super.onResume()

        //showing dialog to load user current transaction
        val builder = AlertDialog.Builder(activity)
        val progressBar: View = layoutInflater.inflate(R.layout.progress_dialog, null)
        builder.setView(progressBar)
        val dialog = builder.create()
        dialog.show()

        //IMPORTANT
        //NOTE : need to make them val to use in observers
        val rippleBackground = rippleBackground
        val linearLayoutOTP = linearLayoutOTP

        val btnPlayStop = btn_play_stop
        val chronometer = chronometer

        val currentTransactionViewModel = ViewModelProviders.of(this).get(CurrentTransactionViewModel::class.java)
        val currentTransactionLiveData = currentTransactionViewModel.getCurrentTransactionLiveData()

        currentTransactionLiveData.observe(activity as LifecycleOwner, Observer { currTransac ->
            if (currTransac != null) {
                if (currTransac.isActive!!) {
                    btnPlayStop.text = "Stop"
                    btnPlayStop.setOnClickListener(onStopCLickedListener)

                    //set chronometer base
                    dbRef.child("Current").child("CurrentTime").setValue(ServerValue.TIMESTAMP).addOnSuccessListener {
                        getCurrentFirebaseTime(object : CallbackInterface<Long> {
                            override fun callback(data: Long) {
                                chronometer.base = SystemClock.elapsedRealtime() - (data - currTransac.startTime_in_milliSec!!)
                                chronometer.start()
                                linearLayoutOTP.visibility = View.INVISIBLE
                                startBlinkAnimation(chronometer, true)
                                rippleBackground.startRippleAnimation()

                                dialog.dismiss()
                            }
                        })
                    }

                } else {
                    btnPlayStop.text = "Start"
                    btnPlayStop.setOnClickListener(onStartCLickedListener)

                    chronometer.base = SystemClock.elapsedRealtime()
                    chronometer.stop()
                    linearLayoutOTP.visibility = View.INVISIBLE
                    startBlinkAnimation(chronometer, false)
                    rippleBackground.stopRippleAnimation()

                    dialog.dismiss()
                }
            } else {
                btnPlayStop.text = "Start"
                btnPlayStop.setOnClickListener(onStartCLickedListener)

                chronometer.base = SystemClock.elapsedRealtime()
                chronometer.stop()
                linearLayoutOTP.visibility = View.INVISIBLE
                startBlinkAnimation(chronometer, false)
                rippleBackground.stopRippleAnimation()

                dialog.dismiss()
            }
        })
    }

    private fun getCurrentFirebaseTime(callbackInterface: CallbackInterface<Long>) {

        dbRef.child("Current").child("CurrentTime").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(activity, "Couldn't get time from server", Toast.LENGTH_SHORT).show()
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                callbackInterface.callback(dataSnapshot.value.toString().toLong())
            }

        })

    }

    private val onStartCLickedListener = View.OnClickListener {

        Toast.makeText(activity, "Pleas provide OTP to admin.", Toast.LENGTH_SHORT).show()

        val otp = generateOTP()
        otpList.add(otp)
        dbRef.child("OTP_List").setValue(otpList)
        dbRef.child("GeneratedOTPs").child(otp.toString()).setValue(GeneratedOTP(uid = firebaseAuth.currentUser?.uid.toString(), isRunning = false))

        txt_OTP.text = otp.toString()
        linearLayoutOTP.visibility = View.VISIBLE
    }

    private val onStopCLickedListener = View.OnClickListener {
        Toast.makeText(activity, "Pleas provide OTP to admin.", Toast.LENGTH_SHORT).show()

        val otp = generateOTP()
        otpList.add(otp)
        dbRef.child("OTP_List").setValue(otpList)

        dbRef.child("GeneratedOTPs").child(otp.toString()).setValue(GeneratedOTP(uid = firebaseAuth.currentUser?.uid.toString(), isRunning = true))

        txt_OTP.text = otp.toString()
        linearLayoutOTP.visibility = View.VISIBLE
    }

    private fun generateOTP(): Int {
        var otp = Random.nextInt(1111, 9999)

        while (otpList.contains(otp)) {
            otp = Random.nextInt(1111, 9999)
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
            item.itemId == R.id.dest_completed_transactions -> Navigation.findNavController(activity as Activity, R.id.nav_host_fragment).navigate(R.id.dest_completed_transactions)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun startBlinkAnimation(chron: Chronometer, bool: Boolean) {
        if (bool) {
            val anim = AlphaAnimation(0.0f, 1.0f)
            anim.duration = 1000 //You can manage the blinking time with this parameter
            anim.startOffset = 20
            anim.repeatMode = Animation.REVERSE
            anim.repeatCount = Animation.INFINITE
            chron.startAnimation(anim)
        } else {
            chron.clearAnimation()
        }
    }
}
