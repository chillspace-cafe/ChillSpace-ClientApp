package chillspace.chillspace.views

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import chillspace.chillspace.R
import chillspace.chillspace.databinding.FragmentProfileBinding
import chillspace.chillspace.models.User
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

        //allowing menu in toolbar
        setHasOptionsMenu(true)

        // data binding
        val binding = DataBindingUtil.bind<FragmentProfileBinding>(view)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        databaseRef = FirebaseDatabase.getInstance().reference.child("User").child(firebaseUser.uid.toString())

        //showing dialog to load user data
        val builder = AlertDialog.Builder(activity)
        val progressBar: View = layoutInflater.inflate(R.layout.progress_dialog, null)
        builder.setView(progressBar)
        val dialog = builder.create()
        dialog.show()

        if (MainActivity.user == null) {
            databaseRef.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                    Toast.makeText(activity,"Can't load user data.", Toast.LENGTH_SHORT).show()
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    binding?.user = dataSnapshot.getValue(User::class.java)
                    MainActivity.user = dataSnapshot.getValue(User::class.java)
                    dialog.dismiss()
                }

            })
        } else {
            binding?.user = MainActivity.user
            dialog.dismiss()
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        edit_name_profile.isEnabled = false
        edit_username_profile.isEnabled = false

        btn_changepassword_profile.setOnClickListener {

            //procedure to change password
            val oldPassword1 = edit_oldpassword_profile.text.toString()
            val newPassword1 = edit_newpassword1_profile.text.toString()
            val newPassword2 = edit_newpassword2_profile.text.toString()

            if (newPassword1 == newPassword2) {
                if (newPassword1.length >= 6) {
                    val credential = EmailAuthProvider.getCredential(firebaseUser.email.toString(), oldPassword1)
                    firebaseUser.reauthenticate(credential)?.addOnCompleteListener { taskAuthenticatingCurrentUser ->
                        if (taskAuthenticatingCurrentUser.isSuccessful) {
                            firebaseUser.updatePassword(newPassword1).addOnCompleteListener { taskNewPasswordSetup ->
                                if (taskNewPasswordSetup.isSuccessful) {
                                    Toast.makeText(activity, "New password successfully set.", Toast.LENGTH_SHORT).show()

                                    //nullify all texts in password edit texts
                                    edit_oldpassword_profile.setText("")
                                    edit_newpassword1_profile.setText("")
                                    edit_newpassword2_profile.setText("")
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

        edit_userDetails.setOnClickListener(editUserDetailsOnClickListener)
    }

    //creating menu in the toolbar
    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_toolbar_emailverif_and_profile, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    //NOTE : You may use NavigationUI if you want to navigate always to the frag with same id as menu id
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item!!.itemId == R.id.logout_emailverif_and_profile) {
            FirebaseAuth.getInstance().signOut()
        }
        return super.onOptionsItemSelected(item)
    }

    private val editUserDetailsOnClickListener = View.OnClickListener {
        if (edit_name_profile.isEnabled && edit_username_profile.isEnabled) {
            databaseRef.child("name").setValue(edit_name_profile.text.toString().trim()).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(activity, "Name updated successfully.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(activity, "Couldn't update name. Please try again.", Toast.LENGTH_SHORT).show()
                }
            }

            databaseRef.child("username").setValue(edit_username_profile.text.toString().trim()).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(activity, "Username updated successfully.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(activity, "Couldn't update username. Please try again.", Toast.LENGTH_SHORT).show()
                }
            }

            edit_userDetails.setImageResource(R.drawable.ic_mode_edit_black_24dp)
            edit_name_profile.isEnabled = false
            edit_username_profile.isEnabled = false
        } else {
            Toast.makeText(activity,"You can your User Details now.",Toast.LENGTH_SHORT).show()
            edit_name_profile.isEnabled = true
            edit_username_profile.isEnabled = true
            edit_userDetails.setImageResource(R.drawable.ic_check_circle_black)
        }
    }
}