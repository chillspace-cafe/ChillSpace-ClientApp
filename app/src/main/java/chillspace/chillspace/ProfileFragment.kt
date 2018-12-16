package chillspace.chillspace


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import chillspace.chillspace.databinding.FragmentProfileBinding
import chillspace.chillspace.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class ProfileFragment : Fragment() {

    lateinit var firebaseAuth: FirebaseAuth
    lateinit var databaseRef: DatabaseReference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        // data binding
        val binding = DataBindingUtil.bind<FragmentProfileBinding>(view)

        firebaseAuth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().reference.child("User").child(firebaseAuth.currentUser?.uid.toString())

        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
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
    }

}
