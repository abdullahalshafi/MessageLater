package com.example.safkat.messagelater.Activity;


import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import com.example.safkat.messagelater.Fragments.HomeFrag;
import com.example.safkat.messagelater.R;

public class MainActivity extends AppCompatActivity {




    private Fragment frag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState==null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container,new HomeFrag(),"HOME_FRAG").commit();
        }
    }



    @Override
    public void onBackPressed() {

        int count = getSupportFragmentManager().getBackStackEntryCount();

        frag=getSupportFragmentManager().findFragmentByTag("HOME_FRAG");
        if(frag != null && frag.isVisible()) {

            super.onBackPressed();
            this.finish();

        }

        if (count == 0 && frag ==null ) {
            frag=getSupportFragmentManager().findFragmentByTag("SEND_MSG");
            if(frag != null && frag.isVisible()) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container,new HomeFrag(),"HOME_FRAG").commit();

            }

            frag=getSupportFragmentManager().findFragmentByTag("LOG_FRAG");
            if(frag != null && frag.isVisible()) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container,new HomeFrag(),"HOME_FRAG").commit();

            }

            frag=getSupportFragmentManager().findFragmentByTag("HOME_FRAG");
            if(frag != null && frag.isVisible()) {
                super.onBackPressed();
                this.finish();
            }

        }

    }

}
