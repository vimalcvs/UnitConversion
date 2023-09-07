package com.vimalcvs.calculator;

import static com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import com.google.android.gms.tasks.Task;
import com.google.android.material.elevation.SurfaceColors;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.appupdate.AppUpdateOptions;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.vimalcvs.calculator.utils.PaymentUtil;

import java.util.Objects;

public class AboutActivity extends AppCompatActivity {

    private static final int MY_REQUEST_CODE = 2048;
    private AppUpdateManager appUpdateManager = null;
    private Task<AppUpdateInfo> appUpdateInfoTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(SurfaceColors.SURFACE_0.getColor(this));
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(SurfaceColors.SURFACE_0.getColor(this)));
        Objects.requireNonNull(getSupportActionBar()).setElevation(0f);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.about_activity);

        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("screen", false)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        init();

        findViewById(R.id.about_app_update).setOnClickListener(view -> {
            appUpdateManager = AppUpdateManagerFactory.create(AboutActivity.this);
            appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
            appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                    try {
                        appUpdateManager.startUpdateFlowForResult(
                                // Pass the intent that is returned by 'getAppUpdateInfo()'.
                                appUpdateInfo,
                                // The current activity making the update request.
                                AboutActivity.this,
                                // Or pass 'AppUpdateType.FLEXIBLE' to newBuilder() for
                                // flexible updates.
                                AppUpdateOptions.newBuilder(IMMEDIATE)
                                        .setAllowAssetPackDeletion(true)
                                        .build(),
                                // Include a request code to later monitor this update request.
                                MY_REQUEST_CODE);
                    } catch (IntentSender.SendIntentException e) {
                        Toast.makeText(AboutActivity.this, getString(R.string.checkNet), Toast.LENGTH_SHORT).show();
                    }
                } else if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_NOT_AVAILABLE) {
                    Toast.makeText(AboutActivity.this, getString(R.string.newest), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                Toast.makeText(AboutActivity.this, getString(R.string.checkNet), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != appUpdateManager) {
            appUpdateManager
                    .getAppUpdateInfo()
                    .addOnSuccessListener(
                            appUpdateInfo -> {
                                if (appUpdateInfo.updateAvailability()
                                        == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                                    // If an in-app update is already running, resume the update.
                                    try {
                                        appUpdateManager.startUpdateFlowForResult(
                                                appUpdateInfo,
                                                AboutActivity.this,
                                                AppUpdateOptions.newBuilder(IMMEDIATE)
                                                        .setAllowAssetPackDeletion(true)
                                                        .build(),
                                                MY_REQUEST_CODE);
                                    } catch (IntentSender.SendIntentException e) {
                                        Toast.makeText(AboutActivity.this, getString(R.string.checkNet), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != appUpdateManager) {
            appUpdateManager = null;
        }
        if (null != appUpdateInfoTask) {
            appUpdateInfoTask = null;
        }
    }

    @SuppressLint("SetTextI18n")
    private void init() {
        findViewById(R.id.about_rate).setOnClickListener(v -> {
            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.yangdai.calc");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });
        findViewById(R.id.about_share).setOnClickListener(v -> {
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.shareContent));
            startActivity(Intent.createChooser(sendIntent, getString(R.string.app_name)));
        });
        findViewById(R.id.about_donate).setOnClickListener(v -> {
            try {
                if (PaymentUtil.isInstalledPackage(this)) {
                    PaymentUtil.startAlipayClient(this, "fkx12941hqcc7gpulzphmee"); // 第二步获取到的字符串
                } else {
                    Intent donateIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://paypal.me/YangDaiDevelpoer?country.x=DE&locale.x=de_DE"));
                    startActivity(donateIntent);
                }
            } catch (Exception e) {
                Toast.makeText(this, "Please install Paypal or Alipay.", Toast.LENGTH_SHORT).show();
            }

        });
        findViewById(R.id.about_github).setOnClickListener(v -> {
            Uri uri = Uri.parse("https://github.com/YangDai-Github/Multi-Calculator-Android");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });
        findViewById(R.id.about_email).setOnClickListener(v -> {
            Intent email = new Intent(Intent.ACTION_SEND);
            email.setType("message/rfc822");
            email.putExtra(Intent.EXTRA_EMAIL, new String[]{"dy15800837435@gmail.com"});
            email.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
            startActivity(Intent.createChooser(email, "feedback"));
        });
        findViewById(R.id.about_privacy_policy).setOnClickListener(v -> {
            Uri uri = Uri.parse("https://note.youdao.com/s/G2D1lqzp");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });
        TextView textView = findViewById(R.id.about_app_version);
        textView.setOnLongClickListener(v -> {
            Toast.makeText(this, getString(R.string.thank), Toast.LENGTH_LONG).show();
            return true;
        });
        findViewById(R.id.about_app_osl).setOnClickListener(v -> {
            OssLicensesMenuActivity.setActivityTitle(getString(R.string.app_osl));
            startActivity(new Intent(this, OssLicensesMenuActivity.class));
        });
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                textView.setText(getString(R.string.app_version) + " "
                        + getPackageManager().getPackageInfo(getPackageName(), PackageManager.PackageInfoFlags.of(0)).versionName);
            } else {
                textView.setText(getString(R.string.app_version) + " "
                        + getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
            }
        } catch (PackageManager.NameNotFoundException e) {
            textView.setText(getString(R.string.app_version) + " ");
        }
        findViewById(R.id.about_app_more).setOnClickListener(v -> {
            Uri uri = Uri.parse("https://play.google.com/store/apps/dev?id=7281798021912275557");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });
    }
}