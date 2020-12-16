package subrota.shuvro.doriduino;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

public class Helper {
    private Context context;
    ProgressDialog pd ;

    public Helper(Context context) {
        this.context = context;
        pd = new ProgressDialog(context);
    }

    public static void showSnackBar(View v, String message) {
        Snackbar.make(v, message, Snackbar.LENGTH_SHORT)
                .setAction("Action", null)
                .show();
    }

    public void progressShow(String message){
        pd.setMessage(message);
        pd.show();
    }

    public void progressDismiss(){
        pd.dismiss();
    }

}
