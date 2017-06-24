package vi.filepicker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import droidninja.filepicker.utils.FragmentUtil;

public class FragmentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        initView();
    }

    private void initView() {
        CallerFragment callerFragment = new CallerFragment();
        FragmentUtil.addFragment(this, R.id.container,callerFragment);
    }
}
