package com.example.hui.testcamera;

import android.graphics.Matrix;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Rational;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;



import androidx.camera.core.CameraX;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.lifecycle.LifecycleOwner;

public class CameraXActivity extends AppCompatActivity {

    private TextureView viewFinder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camerax);
        viewFinder = findViewById(R.id.view_finder);
        viewFinder.post(new Runnable() {
            @Override
            public void run() {
                startCamera();
            }
        });
        viewFinder.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                updataTransform();
            }
        });
    }

    private Runnable startCamera(){
        PreviewConfig.Builder builder = new PreviewConfig.Builder();
        builder.setTargetAspectRatio(new Rational(1, 1));
        builder.setTargetResolution(new Size(640,640));
        PreviewConfig previewConfig = builder.build();
        Preview preview = new Preview(previewConfig);
        preview.setOnPreviewOutputUpdateListener(new Preview.OnPreviewOutputUpdateListener() {
            @Override
            public void onUpdated(Preview.PreviewOutput output) {
                ViewGroup parent =(ViewGroup) viewFinder.getParent();
                parent.removeView(viewFinder);
                parent.addView(viewFinder,0);
                viewFinder.setSurfaceTexture(output.getSurfaceTexture());
                updataTransform();
            }
        });
        CameraX.bindToLifecycle((LifecycleOwner) this,preview);
        return null;
    }

    private void updataTransform(){
        Matrix matrix = new Matrix();
        float centerX = viewFinder.getWidth()/2f;
        float centerY = viewFinder.getHeight()/2f;
        float rotationDegrees = 0;
        switch (viewFinder.getDisplay().getRotation()){
            case Surface.ROTATION_0:
                rotationDegrees = 0;
                break;
            case Surface.ROTATION_90:
                rotationDegrees = 90;
                break;
            case Surface.ROTATION_180:
                rotationDegrees = 180;
                break;
            case Surface.ROTATION_270:
                rotationDegrees = 270;
                break;
        }
        matrix.postRotate(new Float(rotationDegrees),centerX,centerY);
        viewFinder.setTransform(matrix);
    }


}
