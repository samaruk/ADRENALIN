package bd.com.ADRENALIN.view.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import bd.com.ADRENALIN.R;
import bd.com.ADRENALIN.util.PrefManager;

/**
 * Created by mahfuz on 6/24/2017.
 */

public class BaseFragment extends Fragment {

    private PrefManager prefManager;
    private Context context;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getActivity();
        prefManager = new PrefManager(context);
    }

    public PrefManager getPrefManager() {
        return prefManager;
    }

    public void showMsg(String msg) {
        showMsg(msg, Snackbar.LENGTH_LONG);
    }

    public void showMsg(String msg, int duration) {
//        Snackbar.make(getActivity().findViewById(android.R.id.content), msg, duration).show();
        Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), msg, duration);

        // get snackbar view
        View snackbarView = snackbar.getView();

        // change snackbar text color
        int snackbarTextId = com.google.android.material.R.id.snackbar_text;
        TextView textView = (TextView) snackbarView.findViewById(snackbarTextId);
        textView.setTextColor(ContextCompat.getColor(context, R.color.white));

        // change snackbar background
//        snackbarView.setBackgroundColor(Color.MAGENTA);
        snackbar.show();
    }


}
