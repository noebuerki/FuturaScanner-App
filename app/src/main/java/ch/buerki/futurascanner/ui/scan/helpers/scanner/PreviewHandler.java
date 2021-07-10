package ch.buerki.futurascanner.ui.scan.helpers.scanner;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;

public class PreviewHandler {

    private final PreviewView previewView;

    private Camera camera;
    private boolean torchState;
    private ProcessCameraProvider processCameraProvider;

    public PreviewHandler(PreviewView previewView, ComponentActivity componentActivity) {
        this.previewView = previewView;
        getCameraPermission(componentActivity);
        addCameraStreamView(componentActivity);
    }

    private void getCameraPermission(ComponentActivity activity) {

        boolean hasSelfPermission = ContextCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean shouldRequest = activity.shouldShowRequestPermissionRationale(Manifest.permission.CAMERA);

        if (!hasSelfPermission && !shouldRequest) {
            activity.registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (!isGranted) {
                    Toast.makeText(activity.getApplicationContext(), "Missing Permission", Toast.LENGTH_LONG).show();
                }
            }).launch(Manifest.permission.CAMERA);
        }
    }

    private void addCameraStreamView(ComponentActivity componentActivity) {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(componentActivity.getApplicationContext());
        cameraProviderFuture.addListener(() -> {
            try {
                processCameraProvider = cameraProviderFuture.get();
                bindPreview(componentActivity);
            } catch (ExecutionException | InterruptedException e) {
                Toast.makeText(componentActivity.getApplicationContext(), "Fehler beim einbinden der Kameravorschau", Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(componentActivity.getApplicationContext()));
    }

    private void bindPreview(LifecycleOwner lifecycleOwner) {
        Preview preview = new Preview.Builder()
                .build();
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        camera = processCameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview);
    }

    public Bitmap getLatestFrame() {
        return previewView.getBitmap();
    }

    public boolean toggleTorch() {
        if (camera != null) {
            camera.getCameraControl().enableTorch(!torchState);
            torchState = !torchState;
        }
        return torchState;
    }

    public void quit() {
        processCameraProvider.unbindAll();
    }
}
