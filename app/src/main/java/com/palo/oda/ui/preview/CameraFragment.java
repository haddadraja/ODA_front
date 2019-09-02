package com.palo.oda.ui.preview;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
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

import static androidx.constraintlayout.widget.Constraints.TAG;

public class CameraFragment extends Fragment {

    private CameraViewModel mViewModel;
    private RxPermissions rxPermissions;
    private Camera camera;
    private Preview preview;
    private UDP_Client udp_client = new UDP_Client();

    public static CameraFragment newInstance() {
        return new CameraFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
                                        parameters.setPreviewSize(300, 300);
                                        parameters.setPreviewFrameRate(10);
                                        startThread();
                                        camera.setPreviewCallback(new Camera.PreviewCallback() {
                                            @Override
                                            public void onPreviewFrame(byte[] bytes, Camera camera) {
                                                Log.i(TAG, "onPreviewFrame: " + bytes.length);
                                                udp_client.Message = bytes;
                                            }
                                        });

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
}
