package chillspace.chillspace


import android.app.Activity
import android.os.Bundle
import android.view.*
import androidx.core.app.SharedElementCallback
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import chillspace.chillspace.R.id.signInButton_SignInFragment
import kotlinx.android.synthetic.main.fragment_sign_in.*


class SignInFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view : View = inflater.inflate(R.layout.fragment_sign_in, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //proceed to sign up from sign in fragment
        proceedToSignUpTextView_SignInFragment.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_dest_signin_to_dest_signup)
        }
    }

}

