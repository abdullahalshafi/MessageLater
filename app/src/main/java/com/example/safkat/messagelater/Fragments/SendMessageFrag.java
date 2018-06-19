package com.example.safkat.messagelater.Fragments;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.safkat.messagelater.Reciever.AlarmReceiver;
import com.example.safkat.messagelater.R;
import com.example.safkat.messagelater.Class.SaveLog;
import com.example.safkat.messagelater.Class.SaveLogOpenHelper;


import static android.app.Activity.RESULT_OK;

/**
 * Created by Safkat on 7/16/2017.
 */

public class SendMessageFrag extends Fragment implements View.OnClickListener{


    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;
    private EditText numberEditText, messageEditText;
    private Button send,browse;
    private TextView timePickerShowTextView,datePickerShowTextView;
    private long count_timer;
    private boolean check_status=true;


    private String num,message;
    private int timeHour,timeMin,msgDay,msgMonth,msgYear;
    private AlarmManager alarmmanager;
    private boolean checkErrorMsg=true;


    private static final int PICK_CONTACT=1;
    public static final int MULTIPLE_PERMISSIONS = 10;

    String[] permissions= new String[]{
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.SEND_SMS,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.READ_CONTACTS};

    public SendMessageFrag() {

    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.send_message, null);
        setupUI(rootView.findViewById(R.id.SendMessage));

        numberEditText=(EditText) rootView.findViewById(R.id.number);
        messageEditText =(EditText) rootView.findViewById(R.id.msg);


        timePickerShowTextView =(TextView) rootView.findViewById(R.id.timePickerShowTextView);
        datePickerShowTextView =(TextView) rootView.findViewById(R.id.datePickerShowTextView);

        send=(Button) rootView.findViewById(R.id.send);
        browse=(Button) rootView.findViewById(R.id.browse);


        timePickerShowTextView.setOnClickListener(this);
        datePickerShowTextView.setOnClickListener(this);
        browse.setOnClickListener(this);
        send.setOnClickListener(this);

        InitiateTimeAndDate();

        if (checkPermissions()){

        }

        return rootView;
    }

    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.send) {
            num = numberEditText.getText().toString();
            message = messageEditText.getText().toString();

            if (num.length() > 0 ) {

                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.SEND_SMS)) {

                    } else {
                        requestPermissions(new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
                    }
                    if(checkErrorMsg) {
                      //  Toast.makeText(getContext(), "called", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                     initAndSendMessage();

                }


            }
            else if(num.length()==0) {
                numberEditText.setHint("Number Required!");
                numberEditText.setHintTextColor(Color.RED);

            }

        }
        if(v.getId()==R.id.browse) {

            if (ContextCompat.checkSelfPermission(getContext(),Manifest.permission.READ_CONTACTS)!=PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_CONTACTS)) {

                } else {
                   requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PICK_CONTACT);
                }
            }

            if(ContextCompat.checkSelfPermission(getContext(),Manifest.permission.READ_CONTACTS)==PackageManager.PERMISSION_GRANTED) {

                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, PICK_CONTACT);
            }
            else {
                Toast.makeText(getContext(), "Permission Not Granted!", Toast.LENGTH_SHORT).show();
            }
        }

        if(v.getId()==R.id.timePickerShowTextView) {
            check_status=false;
            Calendar calendar=Calendar.getInstance();
            int hour=calendar.get(Calendar.HOUR_OF_DAY);
            int min=calendar.get(Calendar.MINUTE);
            TimePickerDialog timePickerDialog=new TimePickerDialog(getContext(), R.style.DialogTheme, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                    String myFormat = "hh:mm a"; // your own format
                    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                    Calendar cal=Calendar.getInstance();
                    cal.set(Calendar.HOUR_OF_DAY,hourOfDay);
                    cal.set(Calendar.MINUTE,minute);
                    String  formated_time = sdf.format(cal.getTime());
                    timePickerShowTextView.setText(formated_time);

                    timeHour=hourOfDay;
                    timeMin=minute;
                }
            },hour,min,false);
            timePickerDialog.setTitle("Set Time");
            timePickerDialog.show();


        }

        if(v.getId()==R.id.datePickerShowTextView) {
            check_status=false;
            Calendar calendar=Calendar.getInstance();
            int year=calendar.get(Calendar.YEAR);
            int month=calendar.get(Calendar.MONTH);
            final int day=calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog=new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                    msgDay=dayOfMonth;
                    msgMonth=month;
                    msgYear=year;

                    datePickerShowTextView.setText(String.valueOf(dayOfMonth)+"/"+String.valueOf(month+1)+"/"+String.valueOf(year));
                }
            },year,month,day);

            datePickerDialog.setTitle("Set Date");
            datePickerDialog.show();
        }
    }

    private void initAndSendMessage() {
        Calendar current = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();

        if (msgYear == 0 && msgMonth == 0 && msgDay == 0) {
            msgYear = calendar.get(Calendar.YEAR);
            msgMonth = calendar.get(Calendar.MONTH);
            msgDay = calendar.get(Calendar.DAY_OF_MONTH);
        }

        calendar.set(msgYear, msgMonth, msgDay, timeHour, timeMin);

        if (calendar.compareTo(current) <= 0) {
            Toast.makeText(getContext(), "Set Time or Date!", Toast.LENGTH_SHORT).show();
            return;

        } else {
            count_timer = calendar.getTimeInMillis();
        }

        SetAlarm();

        Toast.makeText(getContext(), "Sms queued!", Toast.LENGTH_SHORT).show();
        numberEditText.setText("");
        messageEditText.setText("");
        numberEditText.setHint("Enter Number");
        numberEditText.setHintTextColor(getResources().getColor(R.color.edittextcol));
        check_status = true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_CONTACT) {
            if (resultCode == RESULT_OK) {
                Uri contactData = data.getData();
                String number = "";
                Cursor cursor = getActivity().getContentResolver().query(contactData, null, null, null, null);
                cursor.moveToFirst();
                String hasPhone = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                String contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                if (hasPhone.equals("1")) {
                    Cursor phones = getActivity().getContentResolver().query
                            (ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                            + " = " + contactId, null, null);
                    while (phones.moveToNext()) {
                        number = phones.getString(phones.getColumnIndex
                                (ContactsContract.CommonDataKinds.Phone.NUMBER)).replaceAll("[-() ]", "");
                    }
                    phones.close();

                    this.numberEditText.setText(number);
                    //Do something with number
                } else {
                    Toast.makeText(getContext(), "This contact has no phone number", Toast.LENGTH_LONG).show();
                }
                cursor.close();
            }
        }
    }

    //check permission
    private  boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p:permissions) {
            result = ContextCompat.checkSelfPermission(getContext(),p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(getActivity(), listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),MULTIPLE_PERMISSIONS );
            return false;
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                    initAndSendMessage();
                    checkErrorMsg=false;
                    return;
                }
                else {
                    Toast.makeText(getContext(), "Permission Not Granted!", Toast.LENGTH_SHORT).show();
                }
                break;

            case PICK_CONTACT:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    startActivityForResult(intent, PICK_CONTACT);
                    return;
                }
                break;

            case MULTIPLE_PERMISSIONS:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    // permissions granted.
                } else {
                    String permission = "";
                    for (String per : permissions) {
                        permission += "\n" + per;
                    }

                    Log.d("permissions", "onRequestPermissionsResult: "+permission);
                    // permissions list of don't granted permission
                }
                return;
            }
        }
    }

    private void SetAlarm() {
        alarmmanager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), AlarmReceiver.class);

        final int u_id = (int) System.currentTimeMillis();
        intent.putExtra("num", num);
        intent.putExtra("msg",message);
        intent.putExtra("u_id", String.valueOf(u_id));

        PendingIntent sender = PendingIntent.getBroadcast(getContext(), u_id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmmanager.setExact(AlarmManager.RTC_WAKEUP, count_timer, sender);

        SaveIntoDatabase(num, String.valueOf(count_timer), String.valueOf(u_id));

    }

    private void SaveIntoDatabase(String num,String time,String u_id) {
        SaveLogOpenHelper mDbhelper=new SaveLogOpenHelper(getContext());
        SQLiteDatabase db=mDbhelper.getWritableDatabase();

        ContentValues values=new ContentValues();
        values.put(SaveLog.DefineTable.COLUMN_NAME_PNO,num);
        values.put(SaveLog.DefineTable.COLUMN_NAME_TIME,time);
        values.put(SaveLog.DefineTable.COLUMN_NAME_UID,u_id);
        db.insert(SaveLog.DefineTable.TABLE_NAME,null,values);

    }

    private void InitiateTimeAndDate()  {

        String myFormat = "hh:mm a";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        Calendar cal=Calendar.getInstance();
        cal.get(Calendar.HOUR_OF_DAY);
        cal.get(Calendar.MINUTE);
        String  formated_time = sdf.format(cal.getTime());
        timePickerShowTextView.setText(formated_time);

        int year= cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);

        datePickerShowTextView.setText(String.valueOf(dayOfMonth)+"/"+String.valueOf(month+1)+"/"+String.valueOf(year));

    }


    public void setupUI(View view) {

            if (!(view instanceof EditText)) {
                view.setOnTouchListener(new View.OnTouchListener() {
                    public boolean onTouch(View v, MotionEvent event) {
                        if(check_status) {
                            InitiateTimeAndDate();
                        }

                        return false;
                    }
                });
            }

            if (view instanceof ViewGroup) {
                for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                    View innerView = ((ViewGroup) view).getChildAt(i);
                    setupUI(innerView);
                }
            }


    }
}
