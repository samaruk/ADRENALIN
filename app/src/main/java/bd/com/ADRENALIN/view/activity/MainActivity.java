package bd.com.ADRENALIN.view.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.content.pm.PackageManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import androidx.annotation.IdRes;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.navigation.NavigationView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.facebook.login.LoginManager;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import bd.com.ADRENALIN.network.ApiCallback;
import bd.com.ADRENALIN.network.RetrofitClient;
import bd.com.ADRENALIN.view.fragment.UserProfileFragment;
import bd.com.ADRENALIN.R;
import bd.com.ADRENALIN.pojo.DrawerItem;
import bd.com.ADRENALIN.pojo.ExamType;
import bd.com.ADRENALIN.pojo.NotificationEventModel;
import bd.com.ADRENALIN.pojo.ResponseModel.ResponseJsonGeneric;
import bd.com.ADRENALIN.pojo.User;
import bd.com.ADRENALIN.pojo.content.NoticeSummaryResponse;
import bd.com.ADRENALIN.util.AppConstants;
import bd.com.ADRENALIN.util.ContentUi;
import bd.com.ADRENALIN.util.NoticeChecker;
import bd.com.ADRENALIN.util.NoticeState;
import bd.com.ADRENALIN.util.AppUtils;
import bd.com.ADRENALIN.view.adapter.DrawerAdapter;
import bd.com.ADRENALIN.view.fragment.AboutUsFragment;
import bd.com.ADRENALIN.view.fragment.AdvisorFragment;
import bd.com.ADRENALIN.view.fragment.HomeFragment;
import bd.com.ADRENALIN.view.fragment.MeritListFragment;
import bd.com.ADRENALIN.view.fragment.NoticeFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static bd.com.ADRENALIN.R.id.selectedExamType;

public class MainActivity extends BaseActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.navigation_drawer)
    DrawerLayout drawerLayout;
    @BindView(R.id.drawer_listview)
    ListView drawerListView;
    @BindView(R.id.tvAdvisor)
    TextView tvAdvisor;
    @BindView(R.id.tvAboutUs)
    TextView tvAboutUs;
    @BindView(R.id.tvLogOut)
    TextView tvLogOut;
    @BindView(R.id.tvPoweredBy)
    TextView tvPoweredBy;

    TextView tvSelectedExamType; // Shows in Drawer Navigation View

    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Context context;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private WebSocketClient mWebSocketClient;
    private String collapsingToolbarTitle;

    private User user;

    public static final int MAIN_CONTENT_ID = R.id.main_content;

    /** How often the open home screen asks for new notices. */
    private static final long NOTICE_POLL_MS = 60 * 1000L;
    private static final int REQUEST_NOTIFICATIONS = 71;
    private TextView bellBadge;
    private final Handler noticeHandler = new Handler(Looper.getMainLooper());
    private final Runnable noticePoll = new Runnable() {
        @Override
        public void run() {
            checkNotices();
            noticeHandler.postDelayed(this, NOTICE_POLL_MS);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        user = getPrefManager().getUserInfo();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //checkAndSetNotification();

        //getExamTypesFromApi();

        setupCollapsingToolbar();

        setupNavigationDrawer(toolbar);

        setupNavViewHeader();

        setPoweredBy();
        Intent intent = getIntent();
        if (intent != null) {
            int eventId = intent.getIntExtra(AppConstants.NotificatioEvent.EVENT_ID, 0);
            if (eventId == 0) {
                Log.e("SamarukWebSocket", "No EVENT_ID Received.");
                getExamTypesFromApi();
            } else {
                Log.e("SamarukWebSocket", "EVENT_ID Is " + eventId + ".");
                intent.putExtra(AppConstants.NotificatioEvent.EVENT_ID, 0);
                getNotification(eventId);
            }
        } else {
            getExamTypesFromApi();
        }
        //FirebaseMessaging.getInstance().send(new RemoteMessage());
        //connectWebSocket();

        // New notices: background check with a notification, and the bell on this screen
        NoticeChecker.ensureChannel(context);
        NoticeChecker.schedule(context);
        askNotificationPermission();
    }

    /** Android 13+ asks before an app may show notifications; asked once. */
    private void askNotificationPermission() {
        if (Build.VERSION.SDK_INT < 33) return;
        String permission = "android.permission.POST_NOTIFICATIONS";
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) return;
        android.content.SharedPreferences prefs = getSharedPreferences("adrenalin_notices", MODE_PRIVATE);
        if (prefs.getBoolean("asked_permission", false)) return;
        prefs.edit().putBoolean("asked_permission", true).apply();
        ActivityCompat.requestPermissions(this, new String[]{permission}, REQUEST_NOTIFICATIONS);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.action_notices);
        View bell = item.getActionView();
        if (bell != null) {
            ContentUi.tint((ImageView) bell.findViewById(R.id.bell_icon), R.drawable.ic_cc_bell, R.color.white);
            bellBadge = bell.findViewById(R.id.bell_badge);
            bell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openNotices();
                }
            });
            showBadge(NoticeState.badge(context));
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_notices) {
            openNotices();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        noticeHandler.removeCallbacks(noticePoll);
        noticeHandler.post(noticePoll);
    }

    @Override
    protected void onPause() {
        super.onPause();
        noticeHandler.removeCallbacks(noticePoll);
    }

    private void openNotices() {
        showBadge(0);
        startActivity(new Intent(context, NoticeActivity.class));
        AppUtils.startActivityAnimation(context);
    }

    private void showBadge(int count) {
        if (bellBadge == null) return;
        bellBadge.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
        bellBadge.setText(count > 99 ? "99+" : String.valueOf(count));
    }

    /** Updates the bell badge; a notice that has not rung yet plays the notification sound. */
    private void checkNotices() {
        RetrofitClient.getApiService(context).getNoticeSummary().enqueue(new Callback<NoticeSummaryResponse>() {
            @Override
            public void onResponse(Call<NoticeSummaryResponse> call, Response<NoticeSummaryResponse> response) {
                NoticeSummaryResponse body = response.body();
                if (body == null || body.IsError || isFinishing()) return;
                NoticeState.Result result = NoticeState.update(context, body.Items);
                showBadge(result.unread);
                if (!result.fresh.isEmpty()) {
                    NoticeChecker.playSound(context);
                    showMsg(getString(R.string.lbl_new_notice) + ": " + (body.LatestTitle == null ? "" : body.LatestTitle));
                }
            }

            @Override
            public void onFailure(Call<NoticeSummaryResponse> call, Throwable t) {
            }
        });
    }

    private void getNotification(int eventId) {
        try {
            RetrofitClient.getApiService(context).getNotification(user.getId(), eventId).enqueue(new ApiCallback<ResponseJsonGeneric<NotificationEventModel>>(context,
                    new Callback<ResponseJsonGeneric<NotificationEventModel>>() {
                        @Override
                        public void onResponse(Call<ResponseJsonGeneric<NotificationEventModel>> call, Response<ResponseJsonGeneric<NotificationEventModel>> response) {
                            ResponseJsonGeneric<NotificationEventModel> model = response.body();
                            //                        LOG.e(TAG, examTypeList.toString());
                            if (model.IsError) {
                                if (model.Msg == null) {
                                    model.Msg = "Server Error";
                                }
                                showMsg(model.Msg);
                            } else {
                                NotificationEventModel data = model.Data;
                                ExamType selectedExamType = getPrefManager().getExamTypeSelected();
                                if (selectedExamType == null) {
                                    selectedExamType.setId(data.ExamTypeId);
                                    selectedExamType.setName(data.ExamTypeName);
                                    getPrefManager().setExamTypeSelected(selectedExamType);
                                }

                                tvSelectedExamType.setVisibility(View.VISIBLE);
                                tvSelectedExamType.setText(selectedExamType.getName() + " Exam Type Selected");

                                // Get Next Exam info after getting the Exam Type

                                if (data.Type == 1) {
                                    selectItem(0); // Home, with the notice list on top
                                    openNotices();
                                } else if (data.Type == 2) {
                                    selectItem(0); // Loading Home Fragment from Navigation Drawer
                                } else if (data.Type == 3) {
                                    selectItem(0); // Loading Home Fragment from Navigation Drawer
                                    Intent intent = new Intent(context, AnswerSummaryListActivity.class);
                                    context.startActivity(intent);
                                    AppUtils.startActivityAnimation(context);
                                } else if (data.Type == 4) {
                                    selectItem(0); // Loading Home Fragment from Navigation Drawer
                                    Intent intent = new Intent(context, ExamDiscussionActivity.class);
                                    intent.putExtra(AppConstants.ExamConstants.INTENT_EXAM_ID, data.ExamId);
                                    context.startActivity(intent);
                                    AppUtils.startActivityAnimation(context);
                                }
                            }

                        }

                        @Override
                        public void onFailure(Call<ResponseJsonGeneric<NotificationEventModel>> call, Throwable t) {
                        }
                    }));
        } catch (Exception e) {
            e.printStackTrace();
            showMsg("Server Error");
        }
    }

    private void setPoweredBy() {
        SpannableString ss = new SpannableString(getString(R.string.powered_by));
        ClickableSpan clickableTerms = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.powered_by_url)));
                startActivity(browserIntent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(true);

            }
        };
        ss.setSpan(clickableTerms, 11, getString(R.string.powered_by).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvPoweredBy.setText(ss);
        tvPoweredBy.setMovementMethod(LinkMovementMethod.getInstance());
        tvPoweredBy.setHighlightColor(Color.TRANSPARENT);
    }

    private void getExamTypesFromApi() {

        Log.e("SamarukWebSocket", "EVENT_ID Is " + user.getId());
        RetrofitClient.getApiService(context).getExamTypeByUser(user.getId()).enqueue(new ApiCallback<List<ExamType>>(context,
                new Callback<List<ExamType>>() {
                    @Override
                    public void onResponse(Call<List<ExamType>> call, Response<List<ExamType>> response) {
                        List<ExamType> examTypeList = response.body();
                        Log.e("SamarukWebSocket", "EVENT_ID Is " + examTypeList);
//                        LOG.e(TAG, examTypeList.toString());
                        if (examTypeList != null && !examTypeList.isEmpty() && examTypeList.size() == 1) {
                            ExamType selectedExamType = examTypeList.get(0);
                            getPrefManager().setExamTypeSelected(selectedExamType);

                            tvSelectedExamType.setVisibility(View.VISIBLE);
                            tvSelectedExamType.setText(selectedExamType.getName() + " Exam Type Selected");

                            // Get Next Exam info after getting the Exam Type

                            selectItem(0); // Loading Home Fragment from Navigation Drawer
                        } else if (examTypeList != null && !examTypeList.isEmpty()) {
                            showExamTypeDialog(examTypeList);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<ExamType>> call, Throwable t) {
                    }
                }));
    }

    private void showExamTypeDialog(final List<ExamType> examTypeList) {
        final Dialog dialog = new Dialog(context, R.style.AppTheme_Dark_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);

        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_exam_type_selection, null);
        dialog.setContentView(dialogView);

        RadioGroup rgExamType = dialogView.findViewById(R.id.rgExamType);
        for (ExamType examType : examTypeList) {
            RadioButton radioButton = new RadioButton(context);
            radioButton.setTextSize(18);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 5, 0, 0); //Setting only top margin
            radioButton.setLayoutParams(params);

            radioButton.setId(examType.getId());
            radioButton.setText(examType.getName());

            rgExamType.addView(radioButton);
        }

        rgExamType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                ExamType selectedExamType = new ExamType();
                for (ExamType examType : examTypeList) {
                    if (examType.getId() == i) {
                        selectedExamType = examType;
                        break;
                    }
                }
                // Storing the selected Exam Type to SharedPreference
                getPrefManager().setExamTypeSelected(selectedExamType);

                tvSelectedExamType.setVisibility(View.VISIBLE);
                tvSelectedExamType.setText(selectedExamType.getName() + " Exam Type Selected");

                // Get Next Exam info after getting the Exam Type

                selectItem(0); // Loading Home Fragment from Navigation Drawer

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void selectItem(int position) {

        Fragment fragment = null;
        DialogFragment dialogFragment = null;
        String tag = null;
        collapsingToolbarTitle = null;

        switch (position) {
            case 0:
                fragment = new HomeFragment();
                tag = HomeFragment.TAG;
                collapsingToolbarTitle = getString(R.string.lbl_home);
                break;

            case 1:
                fragment = new UserProfileFragment();
                collapsingToolbarTitle = getString(R.string.lbl_user_profile);
                break;
            case 2:
                fragment = new MeritListFragment();
//                logout();
                collapsingToolbarTitle = getString(R.string.lbl_merit_list);
                break;

            case 3:
                openNotices();
                break;

//            case 4:
//
//                collapsingToolbarTitle = getString(R.string.lbl_alarm);
//                break;

            case 4:
                AppUtils.rateApp(context);
                collapsingToolbarTitle = getString(R.string.lbl_rate_us);
                break;


            default:
                break;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragment != null) {
            fragmentManager.beginTransaction().replace(MAIN_CONTENT_ID, fragment).commit();

            drawerListView.setItemChecked(position, true);
            drawerListView.setSelection(position);
        } else if (dialogFragment != null) {
            dialogFragment.show(fragmentManager, tag);
        }
        drawerLayout.closeDrawers();
    }

    @OnClick({R.id.tvAdvisor, R.id.tvAboutUs, R.id.tvLogOut})
    public void onDrawerOtherItemClick(View view) {
        Fragment fragment = null;
        collapsingToolbarTitle = null;

        switch (view.getId()) {

            case R.id.tvAdvisor:
                fragment = new AdvisorFragment();
                collapsingToolbarTitle = getString(R.string.lbl_advisors);
                break;

            case R.id.tvAboutUs:
                startActivity(new Intent(context, AboutUsActivity.class));
                AppUtils.startActivityAnimation(context);
                break;

            case R.id.tvLogOut:
                logout();
                break;

            default:
                break;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragment != null) {
            fragmentManager.beginTransaction().replace(MAIN_CONTENT_ID, fragment).commit();

//            drawerListView.setItemChecked(position, true);
//            drawerListView.setSelection(position);
        }
        drawerLayout.closeDrawers();
    }

    private void setupNavigationDrawer(Toolbar toolbar) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
//        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_list_white_24dp);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.app_name, R.string.app_name) {
            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we don't want
                // anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we don't want
                // anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        //calling sync state is necessary or else your hamburger icon won't show up.
        actionBarDrawerToggle.syncState();


        ArrayList<DrawerItem> drawerItemsList = new ArrayList<>();
        // Getting the Drawer Item Names
        ArrayList<String> drawerItemNameList = new ArrayList<>();
        Collections.addAll(drawerItemNameList, getResources().getStringArray(R.array.items_array_drawer));
        // Getting the Drawer Item Icons
        ArrayList<Integer> drawerItemIconList = new ArrayList<>();
        int[] iconsIdArray = {
                R.mipmap.ic_home_24dp,
                R.mipmap.ic_user_profile_24dp,
                R.mipmap.ic_merit_list_24dp,
                R.mipmap.ic_notice_24dp,
//                R.mipmap.ic_notice_24dp,
                R.mipmap.ic_rateus_24dp
        };
        for (int iconId : iconsIdArray)
            drawerItemIconList.add(iconId);
        // Putting both icon and text together
        if (drawerItemNameList.size() == drawerItemIconList.size()) {
            for (int i = 0; i < drawerItemNameList.size(); i++) {
                drawerItemsList.add(new DrawerItem(drawerItemIconList.get(i), drawerItemNameList.get(i)));
            }
        }

        drawerListView.setAdapter(new DrawerAdapter(this, 0, drawerItemsList));
        drawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                selectItem(position);
            }
        });

    }

    private void setupNavViewHeader() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View navHeader = navigationView.getHeaderView(0);
        TextView tvName = navHeader.findViewById(R.id.name);
        TextView tvEmail = navHeader.findViewById(R.id.email);
        tvSelectedExamType = navHeader.findViewById(selectedExamType);

        tvName.setText(user.getName());
        tvEmail.setText(user.getEmail());

        tvSelectedExamType.setVisibility(View.GONE);
    }

    private void setupCollapsingToolbar() {
        collapsingToolbarLayout =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(" ");
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);
        collapsingToolbarTitle = getString(R.string.app_name);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle(collapsingToolbarTitle);
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbarLayout.setTitle(" ");
                    isShow = false;
                }
            }
        });

    }

    public void checkAndSetNotification() {
        /*Intent intent = new Intent(context, NotificationIntentService.class);
        intent.putExtra(AppConstants.NotificationConstants.USER_ID, user.getId());

        PendingIntent servicePendingIntent = PendingIntent.getService(this, 0, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 24 * 60 * 60 * 1000, servicePendingIntent);  //set repeating every 24 hours
        ((AlarmManager) getSystemService(ALARM_SERVICE))
                .setRepeating(AlarmManager.RTC_WAKEUP,
                        Calendar.getInstance().getTimeInMillis(),
                        AppConstants.NotificationConstants.INTERVAL,
                        servicePendingIntent);*/
        /*try {
            //FirebaseInstanceId.getInstance().deleteInstanceId();

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("SamarukWebSocket", "FirebaseInstanceId Error :- "+e.getMessage());
        }*/
        //Log.e("SamarukWebSocket", "The Token :- "+new AppFirebaseInstanceIdService().GetToken());
//        connectWebSocket();
    }

    private void logout() {
        AlertDialog.Builder logoutBuilder = new AlertDialog.Builder(context, R.style.AppTheme_Dark_Dialog);
        logoutBuilder.setMessage(getString(R.string.lbl_log_out_confirmation))
                .setPositiveButton(getString(R.string.btn_log_me_out), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getPrefManager().clearPrefManager();
//                        Facebook logout
//                        https://stackoverflow.com/questions/29305232/facebook-sdk-4-for-android-how-to-log-out-programmatically
                        LoginManager.getInstance().logOut();
                        Intent intent = new Intent(context, SignInActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        dialogInterface.dismiss();
                        startActivity(intent);
                        finish();
                        AppUtils.startActivityAnimation(context);
                    }
                })
                .setNegativeButton(getString(R.string.btn_no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setCancelable(true)
                .show();


    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
            return;
        }

        super.onBackPressed();
    }

    private void connectWebSocket() {
        URI uri;
        try {
            uri = new URI("ws://gis.laconicsoft.com/api/EventHandler?UserId=8113740C-C747-4AA8-BDB5-51BEE7556398&appId=2222");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        Log.e("SamarukWebSocket", "Calling successfull ");
        Log.e("SamarukWebSocket", uri.getPath().toString());
        mWebSocketClient = new WebSocketClient(uri, new Draft_17()) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.e("SamarukWebSocket", "Opened successfull ");
            }

            @Override
            public void onMessage(String s) {
                final String message = s;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //TextView textView = (TextView)findViewById(R.id.messages);
                        //textView.setText(textView.getText() + "\n" + message);
                        Log.e("SamarukWebSocket", "Getting Message " + message);
                    }
                });
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.i("SamarukWebSocket", "Closed " + s);
            }

            @Override
            public void onError(Exception e) {
                Log.e("SamarukWebSocket", "Closed " + e.getMessage());
            }
        };
        mWebSocketClient.connect();
    }
}
