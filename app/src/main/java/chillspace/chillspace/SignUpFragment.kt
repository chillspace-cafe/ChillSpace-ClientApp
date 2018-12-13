package chillspace.chillspace


import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.BoringLayout
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import chillspace.chillspace.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_sign_up.*
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_sign_up.view.*


class SignUpFragment : Fragment() {

    companion object {
        var result: Boolean = false
    }

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

            val databaseRef = FirebaseDatabase.getInstance().reference
            val firebaseAuth = FirebaseAuth.getInstance()

            val email = editEmail_SignUpFragment.text.toString().trim()
            val password = editPassword_SignUpFragment.text.toString().trim()
            val username = editUsername_SignUpFragment.text.toString().trim()

            if (password.length >= 6) {
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        //add user to database
                        val user = User(email, username)
                        databaseRef.child("User").child(firebaseAuth.currentUser?.uid.toString()).setValue(user)

                        firebaseAuth.signInWithEmailAndPassword(email, password) //Note : Navigation will automatically be handled by main activity
                    } else {
                        textView_SignUpFragment.text = "Couldn't Sign Up.\nPlease try again."
                    }
                }

            } else {
                textView_SignUpFragment.text = "Password must be atleast 6 character long."
            }
        }
    }

}
