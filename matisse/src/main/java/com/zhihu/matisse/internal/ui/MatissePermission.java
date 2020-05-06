package com.zhihu.matisse.internal.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.zhihu.matisse.R;

/**
 * Created by rae on 2020/4/26.
 * Copyright (c) https://github.com/raedev All rights reserved.
 */
public class MatissePermission extends Fragment {

    public interface onMatissePermissionCallback {
        /**
         * 授权成功
         */
        void onPermissionGrant();
    }

    private onMatissePermissionCallback mOnMatissePermissionCallback;

    public void setOnMatissePermissionCallback(onMatissePermissionCallback onMatissePermissionCallback) {
        mOnMatissePermissionCallback = onMatissePermissionCallback;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // request permission
        Toast.makeText(requireContext(), R.string.error_request_permission, Toast.LENGTH_SHORT).show();
        requestPermissions(new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
        }, 1000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != 1000) return;
        boolean result = true;
        for (int i = 0; i < permissions.length; i++) {
            String permission = permissions[i];
            int grantResult = grantResults[i];
            if (Manifest.permission.CAMERA.equals(permission) || Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permission)) {
                result &= (grantResult == PackageManager.PERMISSION_GRANTED);
            }
        }
        if (mOnMatissePermissionCallback != null && result) {
            mOnMatissePermissionCallback.onPermissionGrant();
        }
    }
}
