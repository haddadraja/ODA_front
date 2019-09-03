package com.palo.oda.ui.preview;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.palo.oda.MainActivity;
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
    private ByteArrayOutputStream baos;
    private UdpClientHandler udpClientHandler;
    public CameraFragment() throws SocketException, UnknownHostException {
    }

    public static CameraFragment newInstance() throws SocketException, UnknownHostException {
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
                                        new AsyncTask<Void,Void,Void>() {
                                            @Override
                                            protected Void doInBackground(Void... voids) {
                                                try {
                                                    new UDP_Client(camera).start();
                                                } catch (SocketException e) {
                                                    e.printStackTrace();
                                                }
                                                return null;
                                            }
                                        }.execute();

                                    }
                                });
                    }
                    ;
                });
    }

    public synchronized byte[] getMessage() {
        return message;
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
        udpClientHandler = new UdpClientHandler(this);

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
    public static class UdpClientHandler extends Handler {
        public static final int UPDATE_STATE = 0;
        public static final int UPDATE_MSG = 1;
        public static final int UPDATE_END = 2;
        private CameraFragment parent;

        public UdpClientHandler(CameraFragment parent) {
            super();
            this.parent = parent;
        }

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){
                case UPDATE_STATE:
                    parent.updateState((String)msg.obj);
                    break;
                case UPDATE_MSG:
                    parent.updateRxMsg((String)msg.obj);
                    break;
                case UPDATE_END:
                    parent.clientEnd();
                    break;
                default:
                    super.handleMessage(msg);
            }

        }


}
