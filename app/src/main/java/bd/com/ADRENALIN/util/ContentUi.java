package bd.com.ADRENALIN.util;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import bd.com.ADRENALIN.R;
import bd.com.ADRENALIN.network.RetrofitClient;

/**
 * Helpers of the content screens (courses, lectures, About Us, notices): server URLs, taka and date
 * formatting, Bangla digits, opening links and loading pictures.
 */
public final class ContentUi {

    private static final String TAKA = "৳";
    private static final char[] BANGLA_DIGITS = {'০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯'};

    private ContentUi() {
    }

    /** Absolute URL of a path the server returns relative to its address (for example "Course/Image/3?v=2"). */
    public static String url(String path) {
        if (TextUtils.isEmpty(path)) return null;
        if (path.startsWith("http://") || path.startsWith("https://")) return path;
        String base = RetrofitClient.BASE_URL;
        if (base.endsWith("/") && path.startsWith("/")) path = path.substring(1);
        return base + path;
    }

    /** Taka with South Asian grouping, e.g. 1,00,000; paisa only when there are any. */
    public static String money(double value) {
        boolean negative = value < 0;
        double abs = Math.abs(value);
        long whole = (long) Math.floor(abs + 0.0000001);
        long paisa = Math.round((abs - whole) * 100);
        if (paisa == 100) {
            whole++;
            paisa = 0;
        }
        String digits = Long.toString(whole);
        StringBuilder grouped = new StringBuilder();
        if (digits.length() > 3) {
            String head = digits.substring(0, digits.length() - 3);
            String tail = digits.substring(digits.length() - 3);
            StringBuilder h = new StringBuilder();
            int count = 0;
            for (int i = head.length() - 1; i >= 0; i--) {
                h.insert(0, head.charAt(i));
                if (++count % 2 == 0 && i > 0) h.insert(0, ',');
            }
            grouped.append(h).append(',').append(tail);
        } else {
            grouped.append(digits);
        }
        if (paisa > 0) grouped.append('.').append(paisa < 10 ? "0" : "").append(paisa);
        return (negative ? "-" : "") + TAKA + grouped;
    }

    /** Latin digits as Bangla digits (the course cards show counts like the design). */
    public static String bangla(String text) {
        if (text == null) return "";
        StringBuilder out = new StringBuilder(text.length());
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            out.append(c >= '0' && c <= '9' ? BANGLA_DIGITS[c - '0'] : c);
        }
        return out.toString();
    }

    public static Date parseIso(String iso) {
        if (TextUtils.isEmpty(iso)) return null;
        String[] patterns = {"yyyy-MM-dd'T'HH:mm:ss'Z'", "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd"};
        for (String pattern : patterns) {
            try {
                SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.US);
                format.setTimeZone(TimeZone.getTimeZone(pattern.length() > 10 ? "UTC" : TimeZone.getDefault().getID()));
                return format.parse(iso);
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    /** "28 Sep 2026, 6:42 PM" in the phone's time zone. */
    public static String dateTime(String iso) {
        Date date = parseIso(iso);
        return date == null ? "" : new SimpleDateFormat("d MMM yyyy, h:mm a", Locale.US).format(date);
    }

    /** "28 Sep 2026". */
    public static String date(String iso) {
        Date date = parseIso(iso);
        return date == null ? "" : new SimpleDateFormat("d MMM yyyy", Locale.US).format(date);
    }

    /** "Just now", "5 min ago", "3 h ago", "Yesterday" or the date. */
    public static String ago(String iso) {
        Date date = parseIso(iso);
        if (date == null) return "";
        long minutes = (System.currentTimeMillis() - date.getTime()) / 60000L;
        if (minutes < 1) return "Just now";
        if (minutes < 60) return minutes + " min ago";
        if (minutes < 24 * 60) return (minutes / 60) + " h ago";
        if (minutes < 48 * 60) return "Yesterday";
        return new SimpleDateFormat("d MMM yyyy", Locale.US).format(date);
    }

    public static String size(long bytes) {
        if (bytes >= 1024L * 1024L) return new DecimalFormat("0.0", DecimalFormatSymbols.getInstance(Locale.US)).format(bytes / (1024.0 * 1024.0)) + " MB";
        if (bytes >= 1024L) return (bytes / 1024L) + " KB";
        return bytes + " B";
    }

    public static int dp(Context context, float value) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, context.getResources().getDisplayMetrics()));
    }

    /** A tinted icon at the start of a text (vector icons are drawn black). */
    public static void icon(TextView view, int drawableRes, int colorRes, int sizeDp) {
        android.graphics.drawable.Drawable drawable = androidx.core.content.ContextCompat.getDrawable(view.getContext(), drawableRes);
        if (drawable == null) return;
        drawable = androidx.core.graphics.drawable.DrawableCompat.wrap(drawable.mutate());
        androidx.core.graphics.drawable.DrawableCompat.setTint(drawable, androidx.core.content.ContextCompat.getColor(view.getContext(), colorRes));
        int size = dp(view.getContext(), sizeDp);
        drawable.setBounds(0, 0, size, size);
        view.setCompoundDrawables(drawable, null, null, null);
    }

    /** Tints an ImageView showing a black vector icon. */
    public static void tint(ImageView view, int drawableRes, int colorRes) {
        android.graphics.drawable.Drawable drawable = androidx.core.content.ContextCompat.getDrawable(view.getContext(), drawableRes);
        if (drawable == null) return;
        drawable = androidx.core.graphics.drawable.DrawableCompat.wrap(drawable.mutate());
        androidx.core.graphics.drawable.DrawableCompat.setTint(drawable, androidx.core.content.ContextCompat.getColor(view.getContext(), colorRes));
        view.setImageDrawable(drawable);
    }

    /** Fills a vertical LinearLayout with one bullet row (item_cc_bullet) per point; returns the number of rows. */
    public static int bullets(android.widget.LinearLayout container, java.util.List<String> points, int iconRes, int colorRes) {
        container.removeAllViews();
        if (points == null) return 0;
        android.view.LayoutInflater inflater = android.view.LayoutInflater.from(container.getContext());
        int count = 0;
        for (String point : points) {
            if (TextUtils.isEmpty(point)) continue;
            View row = inflater.inflate(R.layout.item_cc_bullet, container, false);
            tint((ImageView) row.findViewById(R.id.bullet_icon), iconRes, colorRes);
            ((TextView) row.findViewById(R.id.bullet_text)).setText(point.trim());
            container.addView(row);
            count++;
        }
        return count;
    }

    /** Fills an item_cc_text_card (heading and text); the card is hidden when there is no text. */
    public static void textCard(View card, String title, String body) {
        ((TextView) card.findViewById(R.id.text_card_title)).setText(title);
        ((TextView) card.findViewById(R.id.text_card_body)).setText(body == null ? "" : body.trim());
        card.setVisibility(TextUtils.isEmpty(body) || body.trim().length() == 0 ? View.GONE : View.VISIBLE);
    }

    /** One tappable row (item_cc_file) per file; a tap opens the file. Returns the number of rows. */
    public static int files(final android.widget.LinearLayout container, java.util.List<bd.com.ADRENALIN.pojo.content.ContentFileItem> files) {
        container.removeAllViews();
        if (files == null) return 0;
        android.view.LayoutInflater inflater = android.view.LayoutInflater.from(container.getContext());
        for (final bd.com.ADRENALIN.pojo.content.ContentFileItem file : files) {
            View row = inflater.inflate(R.layout.item_cc_file, container, false);
            String type = file.MimeType == null ? "" : file.MimeType.toLowerCase(Locale.US);
            String name = file.FileName == null ? "" : file.FileName.toLowerCase(Locale.US);
            int icon = type.startsWith("image/") ? R.drawable.ic_cc_image : type.startsWith("video/") ? R.drawable.ic_cc_play
                    : (type.equals("application/pdf") || name.endsWith(".pdf")) ? R.drawable.ic_cc_pdf
                    : (name.endsWith(".ppt") || name.endsWith(".pptx")) ? R.drawable.ic_cc_slides : R.drawable.ic_cc_attach;
            tint((ImageView) row.findViewById(R.id.file_icon), icon, R.color.cc_teal);
            tint((ImageView) row.findViewById(R.id.file_open), R.drawable.ic_cc_open, R.color.cc_muted);
            ((TextView) row.findViewById(R.id.file_name)).setText(file.FileName);
            ((TextView) row.findViewById(R.id.file_size)).setText(file.Size > 0 ? size(file.Size) : "");
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    open(container.getContext(), file.Url);
                }
            });
            container.addView(row);
        }
        return files.size();
    }

    /** Shows the text, or hides the view when there is none. */
    public static void textOrGone(TextView view, CharSequence text) {
        if (TextUtils.isEmpty(text)) {
            view.setVisibility(View.GONE);
        } else {
            view.setText(text);
            view.setVisibility(View.VISIBLE);
        }
    }

    /** Loads a picture from the server (relative or absolute URL) with a placeholder. */
    public static void image(Context context, String path, ImageView view, int placeholder) {
        String full = url(path);
        if (full == null) {
            view.setImageResource(placeholder);
            return;
        }
        if (context instanceof Activity && ((Activity) context).isFinishing()) return;
        Glide.with(context).load(full).placeholder(placeholder).error(placeholder).centerCrop().diskCacheStrategy(DiskCacheStrategy.SOURCE).crossFade().into(view);
    }

    /** Opens a link or a server file in the app that handles it (browser, YouTube, PDF viewer...). */
    public static void open(Context context, String pathOrUrl) {
        String full = url(pathOrUrl);
        if (full == null) return;
        if (!full.contains("://") && !full.startsWith("tel:") && !full.startsWith("mailto:")) full = "http://" + full;
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(full));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, R.string.lbl_open_file_failed, Toast.LENGTH_SHORT).show();
        }
    }

    public static void dial(Context context, String phone) {
        if (TextUtils.isEmpty(phone)) return;
        try {
            context.startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone.replaceAll("[^0-9+]", ""))));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, R.string.lbl_open_file_failed, Toast.LENGTH_SHORT).show();
        }
    }

    public static void email(Context context, String address) {
        if (TextUtils.isEmpty(address)) return;
        try {
            context.startActivity(new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + address.trim())));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, R.string.lbl_open_file_failed, Toast.LENGTH_SHORT).show();
        }
    }

    public static void whatsApp(Context context, String number) {
        if (TextUtils.isEmpty(number)) return;
        open(context, "https://wa.me/" + number.replaceAll("[^0-9]", ""));
    }
}
