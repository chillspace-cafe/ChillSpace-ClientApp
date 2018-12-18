package chillspace.chillspace

import android.app.Activity
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    lateinit var firebaseAuth : FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        //allowing menu in toolbar
        setHasOptionsMenu(true)

        firebaseAuth = FirebaseAuth.getInstance()

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    //creating menu in the toolbar
    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_toolbar_home,menu)
        super.onCreateOptionsMenu(menu, inflater)

        //is true if chronometer is running
        var running : Boolean = false

        btn_start_home.setOnClickListener {
            if(!running){
                chronometer.start()
                running = true
            }
        }

        btn_stop_home.setOnClickListener {
            if(running){
                chronometer.stop()
                running = false
            }
        }
    }

    //NOTE : You may use NavigationUI if you want to navigate always to the frag with same id as menu id
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when{
            item!!.itemId==R.id.logout_home -> firebaseAuth.signOut()
            item.itemId==R.id.dest_profile -> Navigation.findNavController(activity as Activity, R.id.nav_host_fragment).navigate(R.id.dest_profile)
        }
        return super.onOptionsItemSelected(item)
    }
}
