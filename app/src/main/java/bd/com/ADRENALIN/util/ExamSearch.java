package bd.com.ADRENALIN.util;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Locale;

import bd.com.ADRENALIN.R;

/**
 * The search field of an exam list (layout include_exam_search): filters as the student types,
 * shows how many exams match and clears with the cross.
 */
public final class ExamSearch {

    public interface Listener {
        void onQuery(String query);
    }

    private final EditText input;
    private final View clear;
    private final TextView status;

    public ExamSearch(final Activity activity, final Listener listener) {
        input = activity.findViewById(R.id.search_input);
        clear = activity.findViewById(R.id.search_clear);
        status = activity.findViewById(R.id.search_status);
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                clear.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
                listener.onQuery(query());
            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                input.setText("");
            }
        });
        input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodManager keyboard = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (keyboard != null) keyboard.hideSoftInputFromWindow(input.getWindowToken(), 0);
                    input.clearFocus();
                    return true;
                }
                return false;
            }
        });
    }

    public String query() {
        return input.getText().toString().trim();
    }

    /** "5 of 40 exams" while searching, or "No exams match …"; hidden when the field is empty. */
    public void showResult(int shown, int total) {
        String query = query();
        if (query.isEmpty()) {
            status.setVisibility(View.GONE);
            return;
        }
        Context context = status.getContext();
        status.setText(shown == 0 ? context.getString(R.string.lbl_no_exam_match, query)
                : context.getString(R.string.lbl_exam_count_filtered, shown, total));
        status.setVisibility(View.VISIBLE);
    }

    /** True when every word of the query appears in one of the fields (case-insensitive). */
    public static boolean matches(String query, String... fields) {
        if (query == null || query.trim().isEmpty()) return true;
        StringBuilder text = new StringBuilder();
        for (String field : fields) {
            if (field != null) text.append(field.toLowerCase(Locale.US)).append(' ');
        }
        String haystack = text.toString();
        for (String word : query.toLowerCase(Locale.US).trim().split("\\s+")) {
            if (!haystack.contains(word)) return false;
        }
        return true;
    }
}
