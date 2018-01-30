package com.example.safkat.messagelater.Class;

/**
 * Created by Safkat on 7/19/2017.
 */

public class LogDetails {

    private String p_no,time,u_id;

    public LogDetails(String p_no,String time,String u_id) {
        this.p_no=p_no;
        this.time=time;
        this.u_id=u_id;
    }

    public String getP_no() {
        return p_no;
    }
    public String getTime() {
        return time;
    }
    public String getU_id(){
        return u_id;
    }

}
