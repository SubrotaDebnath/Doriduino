package subrota.shuvro.doriduino;

import android.content.Context;

public class SharedPreferences {
    private android.content.SharedPreferences sharedPreferences;
    private android.content.SharedPreferences.Editor editor;
    private static final String MESSAGE = "msg";
    private static final String SPEED = "spd";
    private static final String IS_CHECKED = "ckd";
    private static final String DEFAULT = " ";

    public SharedPreferences(Context ctx) {
        sharedPreferences = ctx.getSharedPreferences("db", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void MessageInfo(String message, int speed, Boolean is_checked){
        editor.putString(MESSAGE, message);
        editor.putInt(SPEED, speed);
        editor.putBoolean(IS_CHECKED,is_checked);
        editor.commit();
    }

    public  String getMessage() {
        return sharedPreferences.getString(MESSAGE, DEFAULT);
    }
    public void setMessage(String message){
        editor.putString(MESSAGE,message);
        editor.commit();
    }

    public int getSpeed() {
        return sharedPreferences.getInt(SPEED,0);
    }
    public void setSpeed(int speed){
        editor.putInt(SPEED, speed);
        editor.commit();
    }

    public  boolean getIsChecked() {
        return sharedPreferences.getBoolean(IS_CHECKED, false);
    }
    public void setIsChecked(Boolean is_checked){
        editor.putBoolean(IS_CHECKED,is_checked);
        editor.commit();
    }

}
