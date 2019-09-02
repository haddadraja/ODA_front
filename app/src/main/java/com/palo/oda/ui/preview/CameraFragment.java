package com.palo.oda.ui.preview;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.palo.oda.R;
import com.palo.oda.service.UDP_Client;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.ByteArrayOutputStream;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class CameraFragment extends Fragment {

    private CameraViewModel mViewModel;
    private RxPermissions rxPermissions;
    private Camera camera;
    private Preview preview;
    private byte[] message;
    private UDP_Client udp_client;
    private Size size = new Size(500,500);
    private ByteArrayOutputStream baos;

    public CameraFragment() throws SocketException, UnknownHostException {
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class Size{
       private int height;
       private int width;
    }
    public static CameraFragment newInstance() throws SocketException, UnknownHostException {
        return new CameraFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        udp_client = new UDP_Client(message);
        rxPermissions = new RxPermissions(this);
        rxPermissions
                .request(Manifest.permission.CAMERA)
                .subscribe(granted -> {
                    if (granted) {
                        rxPermissions.request(Manifest.permission.INTERNET)
                                .subscribe(granted2 -> {
                                    if (granted2) {
                                        camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
                                        Camera.Parameters parameters = camera.getParameters();
                                        parameters.setPreviewFormat(ImageFormat.JPEG);
                                        parameters.setPreviewSize(1280, 720);
                                        parameters.setPreviewFrameRate(10);
                                        startThread();
                                        camera.setPreviewCallback(byteCameraBiConsumer());
                                    }
                                });
                    }
                    ;
                });
    }

    private void startThread() {
        udp_client.start();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(this).get(CameraViewModel.class);
        preview = new Preview(getContext(), camera);
        View rootView = inflater.inflate(R.layout.camera_fragment, container, false);
        ConstraintLayout myLayout = rootView.findViewById(R.id.constraintPreview);
        myLayout.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT));
        //SurfaceView surfaceView =
        myLayout.addView(preview);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // TODO: Use the ViewModel
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    private Camera.PreviewCallback byteCameraBiConsumer(){
        return (bytes, camera) -> {
            //Log.i(TAG, "onPreviewFrame: " + bytes.length);
            YuvImage image = new YuvImage(bytes, ImageFormat.NV21,
                    size.width, size.height, null);
            baos = new ByteArrayOutputStream();
            int jpeg_quality = 80;
            image.compressToJpeg(new Rect(0, 0, size.width, size.height),
                    jpeg_quality, baos);

            udp_client.message = baos.toByteArray();
            //Log.i(TAG, "onPreviewFrame image traitement: " + baos.toByteArray().length);
        };
    }
}
