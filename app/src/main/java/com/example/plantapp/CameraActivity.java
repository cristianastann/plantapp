package com.example.plantapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class CameraActivity extends AppCompatActivity {
    private ImageProcessor imageProcessor;
    private CameraManager cameraManager;
    private CameraDevice cameraDevice;
    private CameraCaptureSession cameraCaptureSession;
    private SurfaceHolder surfaceHolder;

    private static final int CAMERA_REQUEST_CODE = 1234;

    private ActivityResultLauncher<String> requestPermissionLauncher;
    private ActivityResultLauncher<String> mGetContentLaunch;

    private void captureImage() {
        try {
            CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(surfaceHolder.getSurface());
            cameraCaptureSession.stopRepeating();
            cameraCaptureSession.capture(captureBuilder.build(), captureCallback, null);
        } catch (CameraAccessException e) {
            Log.e("CameraActivity", "Camera access error", e);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
        SurfaceView surfaceView = findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(surfaceCallback);

        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> navigateToDashboard());

        Button importImageButton = findViewById(R.id.btn_import_image);
        importImageButton.setOnClickListener(v -> openGallery());

        imageProcessor = new ImageProcessor();
        imageProcessor.initializeInterpreter(getAssets(), "assets/plantnet_model.tflite");

        mGetContentLaunch = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                String identifiedPlant = imageProcessor.identifyPlant(bitmap);
            } catch (IOException e) {
                Log.e("CameraActivity", "Image import error", e);
            }
        });

        Button captureButton = findViewById(R.id.btn_capture);
        captureButton.setOnClickListener(v -> captureImage());

        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> openGallery());
    }

    private void navigateToDashboard() {
        Intent intent = new Intent(CameraActivity.this, HomeDashboard.class);
        startActivity(intent);
        finish();
    }

    private void openGallery() {
        mGetContentLaunch.launch("image/*");
    }

    private void openCamera() {
        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
                return;
            }
            cameraManager.openCamera(cameraId, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice camera) {
                    cameraDevice = camera;
                    startPreview();
                }
                @Override
                public void onDisconnected(@NonNull CameraDevice camera) {
                    camera.close();
                    cameraDevice = null;
                }
                @Override
                public void onError(@NonNull CameraDevice camera, int error) {
                    camera.close();
                    cameraDevice = null;
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void startPreview() {
        try {
            Surface surface = surfaceHolder.getSurface();
            CaptureRequest.Builder previewBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            previewBuilder.addTarget(surface);
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    cameraCaptureSession = session;
                    try {
                        cameraCaptureSession.setRepeatingRequest(previewBuilder.build(), null, null);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    private String saveBitmapToFile(Bitmap bitmap) {
        String fileName = "captured_image.jpg"; //
        File storageDir = getApplicationContext().getFilesDir();
        File imageFile = new File(storageDir, fileName);

        try (FileOutputStream out = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imageFile.getAbsolutePath();
    }

    private final CameraCaptureSession.CaptureCallback captureCallback = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            Bitmap capturedBitmap = null;
            String filePath = saveBitmapToFile(capturedBitmap);

            File imageFile = new File(filePath);
            capturedBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            String identifiedPlant = imageProcessor.identifyPlant(capturedBitmap);
            Intent intent = new Intent(CameraActivity.this, ProcessedPlantActivity.class);
            intent.putExtra("identifiedPlant", identifiedPlant);
            startActivity(intent);
        }
    };

    private final SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            openCamera();
        }
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (cameraDevice != null) {
                cameraDevice.close();
            }
        }
    };
}
