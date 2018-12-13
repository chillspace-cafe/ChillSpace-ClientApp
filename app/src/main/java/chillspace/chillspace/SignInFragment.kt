package chillspace.chillspace


import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_sign_in.*


class SignInFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_sign_in, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //proceed to sign up from sign in fragment
        proceedToSignUpTextView_SignInFragment.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_dest_signin_to_dest_signup)
        }

        signInButton_SignInFragment.setOnClickListener {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(editEmail_signInFragment.text.toString().trim(), editPassword_signInFragment.text.toString().trim())
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(activity, "Signed In", Toast.LENGTH_SHORT).show()
                        } else {
                            textView_SignInFragment.text = "Please try again."
                        }
                    }
            //Navigation will be done by main activity
        }

    }
}

