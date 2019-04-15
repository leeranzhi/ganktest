package com.demo.ganktest;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MitoActivity extends AppCompatActivity {
    public static final String MITO_NAME = "mito_name";
    public static final String MITO_IMAGE_URL = "mito_image_url";
    Bitmap imageBitmap = null;
    String mitoName = null;
    String mitoImageUrl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mito);
//        Toolbar toolbar=(Toolbar)findViewById(R.id.mito_toolbar);
//        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        mitoName = intent.getStringExtra(MITO_NAME);
        mitoImageUrl = intent.getStringExtra(MITO_IMAGE_URL);
        final ImageView mitoImageView = (ImageView) findViewById(R.id.picture);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        ActionBar actionBar = getSupportActionBar();
//        Log.d("MitoActivity","观察对象是否为空"+actionBar);
//        if (actionBar != null) {
//            actionBar.setDisplayHomeAsUpEnabled(true);
//        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(MitoActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED) {
                requestAlertWindowPermissions();
            } else {
                Toast.makeText(this, "长按图片即可保存",
                        Toast.LENGTH_LONG).show();
            }
        }
//        setSupportActionBar(toolbar);
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setDisplayHomeAsUpEnabled(true);
//        }
        Glide.with(this).load(mitoImageUrl).centerCrop().into(mitoImageView);
        //图片长按事件
        mitoImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder
                        (MitoActivity.this);
                builder.setItems(new String[]{"保存图片"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mitoImageView.setDrawingCacheEnabled(true);
                        imageBitmap = mitoImageView.getDrawingCache();
                        if (imageBitmap != null) {
                            saveImageToGallery(MitoActivity.this, imageBitmap);
                        }
                    }
                });
                builder.show();
                return true;

            }
        });
        //图片的单击事件
        mitoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 请求权限
     */
    private void requestAlertWindowPermissions() {
        ActivityCompat.requestPermissions(MitoActivity.this, new String[]
                {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }

    /**
     * 权限请求结果回调
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.
                        PERMISSION_GRANTED) {
                    if (imageBitmap != null) {
                        saveImageToGallery(MitoActivity.this, imageBitmap);
                    }
                } else {
                    Toast.makeText(this, "拒绝权限将无法正常保存！",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }

    /**
     * 指定保存路径
     *
     * @param context
     * @param imageBitmap
     */
    public static void saveImageToGallery(MitoActivity context, Bitmap imageBitmap) {
        //首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), "GankTest");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            Log.d("context", "成功保存写入！！！！！！");
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        //其次把图片插入系统图库
//        try {
//            MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(),
//                    fileName, null);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
        //最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.parse("file://" + appDir)));
        Toast.makeText(context, "保存成功至" + appDir, Toast.LENGTH_SHORT).show();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
