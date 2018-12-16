package chillspace.chillspace


import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import chillspace.chillspace.databinding.FragmentProfileBinding
import chillspace.chillspace.models.User
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_profile.*


class ProfileFragment : Fragment() {

    lateinit var firebaseUser: FirebaseUser
    lateinit var databaseRef: DatabaseReference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // data binding
        val binding = DataBindingUtil.bind<FragmentProfileBinding>(view)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        databaseRef = FirebaseDatabase.getInstance().reference.child("User").child(firebaseUser.uid.toString())

        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(activity, "Cant find user data.", Toast.LENGTH_SHORT).show()
            }

            override fun onDataChange(p0: DataSnapshot) {
                val user: User = p0.getValue(User::class.java)!!

                //setting value in data binding
                binding?.user = user
            }

        })

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_changepassword_profile.setOnClickListener {

            //procedure to change password
            val oldPassword1 = edit_oldpassword_profile.text.toString()
            val newPassword1 = edit_newpassword1_profile.text.toString()
            val newPassword2 = edit_newpassword2_profile.text.toString()

            if (newPassword1.equals(newPassword2)) {
                if (newPassword1.length >= 6) {
                    val credential = EmailAuthProvider.getCredential(firebaseUser.email.toString(), oldPassword1)
                    firebaseUser.reauthenticate(credential)?.addOnCompleteListener { taskAuthenticatingCurrentUser ->
                        if (taskAuthenticatingCurrentUser.isSuccessful) {
                            firebaseUser.updatePassword(newPassword1).addOnCompleteListener { taskNewPasswordSetup ->
                                if (taskNewPasswordSetup.isSuccessful) {
                                    Toast.makeText(activity, "New password successfully set.", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(activity, "Couldn't set new password. Please try again.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Toast.makeText(activity, "Old password provided didn't work. Try again.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(activity, "Please enter password with length greater than 6.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(activity, "Passwords don't match.", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
