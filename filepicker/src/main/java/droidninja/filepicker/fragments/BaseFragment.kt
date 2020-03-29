package droidninja.filepicker.fragments

import androidx.fragment.app.Fragment
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

/**
 * A simple [Fragment] subclass.
 */
abstract class BaseFragment : Fragment() {

    open val uiScope = CoroutineScope(Dispatchers.Main)

    companion object {

        val FILE_TYPE = "FILE_TYPE"
    }
}// Required empty public constructor
