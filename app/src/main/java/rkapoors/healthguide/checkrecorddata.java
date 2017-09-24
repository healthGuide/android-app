package rkapoors.healthguide;

/**
 * Created by KAPOOR's on 16-09-2017.
 */

public class checkrecorddata {
    String dt;
   String tm;
     String comment;
     String glucoreading;
    String othercm;

    public String getdt() {
        return dt;
    }
    public void setdt(String dt) {
        this.dt = dt;
    }

    public String gettm(){
        return tm;
    }
    public void settm(String tm){
        this.tm=tm;
    }

    public String getcomment(){
        return comment;
    }
    public void setcomment(String cm){
        this.comment=cm;
    }

    public String getglucoreading(){
        return glucoreading;
    }
    public void setglucoreading(String gr){
        this.glucoreading=gr;
    }

    public String getothercm(){
        return othercm;
    }
    public void setothercm(String ocm){
        this.othercm=ocm;
    }
}
