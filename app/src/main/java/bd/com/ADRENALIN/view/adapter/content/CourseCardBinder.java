package bd.com.ADRENALIN.view.adapter.content;

import android.graphics.Paint;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import bd.com.ADRENALIN.R;
import bd.com.ADRENALIN.pojo.content.Course;
import bd.com.ADRENALIN.util.ContentUi;

/** Fills a card_course view: the course card of All Courses (also the header of Course Details). */
public final class CourseCardBinder {

    private static final String CLASS_WORD = "ক্লাস";
    private static final String TEST_WORD = "টেস্ট";

    private CourseCardBinder() {
    }

    public static void bind(View card, Course c, boolean fullDescription) {
        ImageView image = card.findViewById(R.id.course_image);
        ContentUi.image(card.getContext(), c.ImageUrl, image, R.drawable.bg_cc_banner);

        TextView status = card.findViewById(R.id.course_status);
        status.setText(TextUtils.isEmpty(c.StatusText) ? "RUNNING" : c.StatusText.toUpperCase());
        status.setBackgroundResource(c.Status == 1 ? R.drawable.bg_cc_badge_upcoming : c.Status == 3 ? R.drawable.bg_cc_badge_completed : R.drawable.bg_cc_badge_running);

        ((TextView) card.findViewById(R.id.course_title)).setText(TextUtils.isEmpty(c.Title) ? c.Name : c.Title);
        ContentUi.textOrGone((TextView) card.findViewById(R.id.course_batch), c.BatchLabel);

        TextView description = card.findViewById(R.id.course_description);
        ContentUi.textOrGone(description, c.ShortDescription);
        description.setMaxLines(fullDescription ? Integer.MAX_VALUE : 3);

        String plus = c.CountsAreMinimum ? "+" : "";
        TextView classes = card.findViewById(R.id.course_classes);
        TextView tests = card.findViewById(R.id.course_tests);
        ContentUi.textOrGone(classes, c.ClassCount > 0 ? ContentUi.bangla(c.ClassCount + plus) + " " + CLASS_WORD : null);
        ContentUi.textOrGone(tests, c.TestCount > 0 ? ContentUi.bangla(c.TestCount + plus) + " " + TEST_WORD : null);
        ContentUi.icon(classes, R.drawable.ic_cc_play, R.color.cc_teal, 16);
        ContentUi.icon(tests, R.drawable.ic_cc_test, R.color.cc_teal, 16);
        card.findViewById(R.id.course_chips).setVisibility(c.ClassCount > 0 || c.TestCount > 0 ? View.VISIBLE : View.GONE);

        TextView price = card.findViewById(R.id.course_price);
        price.setText(c.Price > 0 ? ContentUi.money(c.Price) : card.getContext().getString(R.string.lbl_free));
        TextView regular = card.findViewById(R.id.course_regular_price);
        if (c.RegularPrice > c.Price) {
            regular.setText(ContentUi.money(c.RegularPrice));
            regular.setPaintFlags(regular.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            regular.setVisibility(View.VISIBLE);
        } else {
            regular.setVisibility(View.GONE);
        }
        ContentUi.textOrGone((TextView) card.findViewById(R.id.course_discount), c.DiscountLabel);

        List<String> meta = new ArrayList<>();
        if (!TextUtils.isEmpty(c.Duration)) meta.add(c.Duration);
        if (!TextUtils.isEmpty(c.StartDate)) meta.add(card.getContext().getString(R.string.lbl_starts_value, ContentUi.date(c.StartDate)));
        TextView metaView = card.findViewById(R.id.course_meta);
        ContentUi.textOrGone(metaView, TextUtils.join("  ·  ", meta));
        ContentUi.icon(metaView, R.drawable.ic_cc_clock, R.color.cc_slate, 15);
    }
}
