package com.example.safkat.messagelater.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.safkat.messagelater.R;

/**
 * Created by Safkat on 7/16/2017.
 */

public class HomeFrag extends Fragment {

    private Button SendMsg,Log;

    public HomeFrag() {

    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.home_frag, null);

        SendMsg=(Button) rootView.findViewById(R.id.SendMessage);
        Log=(Button) rootView.findViewById(R.id.Log);

        SendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.container,new SendMessageFrag(),"SEND_MSG").commit();

            }
        });

        Log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.container,new LogFrag(),"LOG_FRAG").commit();

            }
        });

        return rootView;
    }

}
