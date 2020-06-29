package vi.filepicker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import droidninja.filepicker.utils.FragmentUtil.addFragment

class FragmentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment)
        initView()
    }

    private fun initView() {
        val callerFragment = CallerFragment()
        addFragment(this, R.id.container, callerFragment)
    }
}