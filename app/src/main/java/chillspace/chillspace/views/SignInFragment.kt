package chillspace.chillspace.views


import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import chillspace.chillspace.R
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
            Navigation.findNavController(it).navigate(R.id.dest_signup)
        }

        signInButton_SignInFragment.setOnClickListener {

            //showing progress dialog
            val builder = AlertDialog.Builder(activity)
            val progressBar: View = layoutInflater.inflate(R.layout.progress_dialog, null)
            builder.setView(progressBar)
            val dialog = builder.create()
            dialog.show()

            FirebaseAuth.getInstance().signInWithEmailAndPassword(editEmail_signInFragment.text.toString().trim(), editPassword_signInFragment.text.toString().trim())
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(activity, "Signed In", Toast.LENGTH_SHORT).show()
                        } else {
                            textView_SignInFragment.text = "Please try again."
                        }

                        dialog.dismiss()
                    }
            //Navigation will be done by main activity
        }


        text_forgot_password.setOnClickListener {
            val builder = AlertDialog.Builder(activity)
            val forgotPasswordView: View = layoutInflater.inflate(R.layout.forgotpasswordlayout, null)
            builder.setView(forgotPasswordView)
            val dialog = builder.create()
            dialog.show()

            val resetPasswordButton = forgotPasswordView.findViewById<Button>(R.id.btn_reset_password_forgot_password)
            val cancelButton = forgotPasswordView.findViewById<Button>(R.id.btn_cancel_forgot_password)
            val editEmail = forgotPasswordView.findViewById<EditText>(R.id.edit_email_forgot_password)

            resetPasswordButton.setOnClickListener {
                dialog.dismiss()
                Toast.makeText(activity, "Sending password reset email.", Toast.LENGTH_SHORT).show()
                FirebaseAuth.getInstance().sendPasswordResetEmail(editEmail.text.toString()).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(activity, "Password reset email sent.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(activity, "Could not send password reset email sent.", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            cancelButton.setOnClickListener {
                dialog.dismiss()
            }
        }
    }
}

