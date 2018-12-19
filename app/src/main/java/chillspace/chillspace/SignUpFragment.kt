package chillspace.chillspace


import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_sign_up.*
import com.google.firebase.database.*
import com.google.firebase.database.DatabaseError
import chillspace.chillspace.models.User
import chillspace.chillspace.CallbackInterface
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.FirebaseDatabase


class SignUpFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_sign_up, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //proceed to sign in from sign up
        proceedToSignInTextView_SignUpFragment.setOnClickListener {
            Navigation.findNavController(it).popBackStack()
        }

        //registering user
        buttonRegister_SignUpFragment.setOnClickListener { view ->

            //showing progress dialog
            val builder = AlertDialog.Builder(activity)
            var progressBar: View = layoutInflater.inflate(R.layout.progress_dialog, null)
            builder.setView(progressBar)
            val dialog = builder.create()
            dialog.show()

            val databaseRef = FirebaseDatabase.getInstance().reference
            val firebaseAuth = FirebaseAuth.getInstance()

            val email = editEmail_SignUpFragment.text.toString().trim()
            val password = editPassword_SignUpFragment.text.toString().trim()
            val username = editUsername_SignUpFragment.text.toString().trim()

            isUsernamePreExisting(databaseRef, username, object : CallbackInterface<Boolean>{
                override fun callback(data: Boolean) { //data shows isUsernamePreExisting

                    //username doesn't exists
                    if(!data){
                        if (password.length >= 6) {
                            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                                if (it.isSuccessful) {
                                    //add user to database
                                    val user = User(email, username)
                                    databaseRef.child("User").child(firebaseAuth.currentUser?.uid.toString()).setValue(user)

                                    firebaseAuth.signInWithEmailAndPassword(email, password) //Note : Navigation will automatically be handled by main activity
                                    dialog.dismiss()
                                } else {
                                    textView_SignUpFragment.text = "Couldn't Sign Up.\nPlease try again."
                                    dialog.dismiss()
                                }
                            }

                        } else {
                            textView_SignUpFragment.text = "Password must be atleast 6 character long."
                            dialog.dismiss()
                        }
                    }

                    //passed username exist
                    else{
                        dialog.dismiss()
                        textView_SignUpFragment.text = "Username already exists."
                    }
                }

            })

        }
    }

    private fun isUsernamePreExisting(databaseRef: DatabaseReference, username: String, @NonNull callbackInterface: CallbackInterface<Boolean>) {

        databaseRef.child("User")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (snapshot in dataSnapshot.children) {
                            val user = snapshot.getValue(User::class.java)
                            if (user?.username.equals(username)) {
                                //username found
                                callbackInterface.callback(true)
                                return
                            }
                        }

                        //username not found
                        callbackInterface.callback(false)
                    }
                    override fun onCancelled(databaseError: DatabaseError) {}
                })

    }
}
