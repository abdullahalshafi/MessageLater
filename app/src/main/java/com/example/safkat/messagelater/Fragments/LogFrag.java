package com.example.safkat.messagelater.Fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.safkat.messagelater.Reciever.AlarmReceiver;
import com.example.safkat.messagelater.Class.LogDetails;
import com.example.safkat.messagelater.R;
import com.example.safkat.messagelater.Class.SaveLog;
import com.example.safkat.messagelater.Class.SaveLogOpenHelper;

import java.text.DateFormat;
import java.util.ArrayList;

/**
 * Created by Safkat on 7/16/2017.
 */

public class LogFrag extends Fragment {

    private Cursor res;
    private String p_no,time,u_id;

    private ArrayList<LogDetails> myList=new ArrayList<>();
    private ArrayAdapter<LogDetails> myAdapter;

    private RecyclerView recyclerView;

    public LogFrag() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeList();
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.log_frag, null);

        recyclerView=(RecyclerView) rootView.findViewById(R.id.recycleView);

        //Setting the Ui parameters of the Recycle View
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        if(myList.size()>0 && recyclerView != null) {
            //Setting the Adapter of the recycle view
            recyclerView.setAdapter(new MyAdapter(myList));
        }

        recyclerView.setLayoutManager(layoutManager);



        return rootView;
    }



    private  class MyAdapter extends RecyclerView.Adapter<MyViewHolder>{

        private ArrayList<LogDetails> list;
        private String u_id;

        public MyAdapter(ArrayList<LogDetails> data) {
            this.list=data;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.log_item,parent,false);

            //calling the custom view holder class

            MyViewHolder holder=new MyViewHolder(v);


            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder,final int position) {

            //setting the parameters for the views

            long getTime=Long.parseLong(list.get(position).getTime());

            String finalTime=DateFormat.getDateTimeInstance().format(getTime);

            holder.p_no.setText(list.get(position).getP_no()); //using the EventModel class to get the values of ArrayList
            holder.timer.setText(finalTime);



            holder.cancelBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    AlertDialog.Builder builder=new AlertDialog.Builder(v.getContext());
                    builder.setTitle("Cancel")
                            .setMessage("Do you really want to cancel?")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    u_id=list.get(position).getU_id();
                                    cancelMessage(Integer.valueOf(u_id));
                                    deleteLogFromDatabse(u_id,v.getContext());

                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.dismiss();
                                }
                            })
                            .show();
                }
            });


        }

        @Override
        public int getItemCount() {
            return list.size();
        }

    }

    private static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView p_no,timer;
        private Button cancelBt;

        public MyViewHolder(View v) {
            super(v);

            //initializing the views
            p_no=(TextView)v.findViewById(R.id.p_no);
            timer=(TextView)v.findViewById(R.id.time);
            cancelBt=(Button) v.findViewById(R.id.cancelBt);

        }
    }

    private void initializeList() {

        SaveLogOpenHelper mDbhelper=new SaveLogOpenHelper(getContext());
        SQLiteDatabase db=mDbhelper.getReadableDatabase();
        String query = "SELECT * FROM " + SaveLog.DefineTable.TABLE_NAME;

        Cursor res = db.rawQuery(query,null);

        while (res.moveToNext()) {
            p_no=res.getString(0);
            time=res.getString(1);
            u_id=res.getString(2);

            myList.add(new LogDetails(p_no,time,u_id));
        }

    }

    private  void deleteLogFromDatabse(String u_id,Context context) {

        SaveLogOpenHelper mDbhelper=new SaveLogOpenHelper(context);
        SQLiteDatabase db=mDbhelper.getWritableDatabase();

        String selection= SaveLog.DefineTable.COLUMN_NAME_UID + " LIKE ?";
        String [] selectionArgs={u_id};
        db.delete(SaveLog.DefineTable.TABLE_NAME,selection,selectionArgs);

        getFragmentManager().beginTransaction().replace(R.id.container,new LogFrag(),"LOG_FRAG").commit();
    }

    private void cancelMessage(int u_id) {

        Intent intent = new Intent(getContext(), AlarmReceiver.class);
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), u_id, intent, PendingIntent.FLAG_NO_CREATE);
        alarmManager.cancel(pendingIntent);
    }

}
