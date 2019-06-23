package com.example.hui.testcamera;

import android.Manifest;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.qw.soul.permission.SoulPermission;
import com.qw.soul.permission.bean.Permission;
import com.qw.soul.permission.bean.Permissions;
import com.qw.soul.permission.callbcak.CheckRequestPermissionListener;
import com.qw.soul.permission.callbcak.CheckRequestPermissionsListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import cn.jzvd.JZMediaInterface;
import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;

public class MainActivity extends AppCompatActivity {

    public static final int TAKE_PHOTO = 1;
    private ImageView picture;
    private Uri imageUri;
    JzvdStd jzvdStd;
    private Button videoButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera/VID_20190622_115322.mp4";
        Button takePhoto = (Button) findViewById(R.id.take_photo);
        picture = findViewById(R.id.picture);
        videoButton = findViewById(R.id.videoButton);
        videoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                File file = new File(path);
                Uri uri = Uri.parse(path);
                intent.setDataAndType(uri, "video/*");
                startActivity(intent);
            }
        });

        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File outputImage = new File(getExternalCacheDir(),
                        "output_image.jpg");
                try {
                    if(outputImage.exists()){
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                }catch (IOException e){
                    e.printStackTrace();
                }
                if(Build.VERSION.SDK_INT>=24){
                    imageUri = FileProvider.getUriForFile(MainActivity.this,
                            "com.example.testcamera.fileprovider",outputImage);
                }else {
                    imageUri = Uri.fromFile(outputImage);
                }

                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                startActivityForResult(intent,TAKE_PHOTO);


            }
        });
        jzvdStd = (JzvdStd) findViewById(R.id.jz_video);
        SoulPermission.getInstance().checkAndRequestPermissions(Permissions.build(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), new CheckRequestPermissionsListener() {
            @Override
            public void onAllPermissionOk(Permission[] allPermissions) {
                jzvdStd.setUp(path,"视频播放");
            }

            @Override
            public void onPermissionDenied(Permission[] refusedPermissions) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case TAKE_PHOTO:
                if(resultCode == RESULT_OK){
                    Bitmap bitmap = null;
                    try {
                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    picture.setImageBitmap(bitmap);
                }
                break;
             default:
                 break;
        }
    }

    private long getVideoLong(String path){

        return 0;
    }

    @Override
    public void onBackPressed() {
        if (Jzvd.backPress()) {
            return;
        }
        super.onBackPressed();
    }

}
