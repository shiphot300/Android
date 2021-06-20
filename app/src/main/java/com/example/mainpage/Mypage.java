package com.example.mainpage;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mainpage.write.PhotoAdapter;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;


public class Mypage extends Fragment {
    public static int OPEN_IMAGE_REQUEST_CODE = 49018;
    private RelativeLayout mBtnAddCamera;
    private RecyclerView rvContentView;
    private PhotoAdapter photoAdapter;
    private TextView tvPhotoLabel;
    private Button btn_product;
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.mypage, container, false);

        mBtnAddCamera = view.findViewById(R.id.view_camera);
        rvContentView = view.findViewById(R.id.rv_content);
        tvPhotoLabel = view.findViewById(R.id.tv_photo_number);
        btn_product = view.findViewById(R.id.et_title1);
        
        btn_product.setOnClickListener(new View.OnClickListener() { //판매내역페이지로 이동

            @Override
            public void onClick(View v) {
                Log.d(String.valueOf(btn_product), "정상작동: ");
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                Myproduct myProduct = new Myproduct();
                transaction.replace(R.id.main_frame, myProduct);
                transaction.commit();
                Log.d(String.valueOf(btn_product), "정상작동: ");

            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRecyclerView();
        mBtnAddCamera.setOnClickListener(view1 -> getGallaryIntent());
    }

    private void initRecyclerView() {

        photoAdapter = new PhotoAdapter(getActivity()) {
            @Override
            public void itemCallback(int position) {
                photoAdapter.removeItem(position);
                updatePhotoIndexLabel();
            }
        };
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
        rvContentView.setLayoutManager(linearLayoutManager);
        rvContentView.setAdapter(photoAdapter);
    }

    private void updatePhotoIndexLabel() {
        if (photoAdapter.getItemList() == null) {
            tvPhotoLabel.setText(String.format("%s/%s",0,10));
        } else {
            int registerPhotoAmount = photoAdapter.getItemList().size();
            tvPhotoLabel.setText(String.format("%s/%s",registerPhotoAmount,10));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OPEN_IMAGE_REQUEST_CODE) {
            if (data == null) {
                return;
            }

            Uri uri = data.getData();
            photoAdapter.addItem(String.valueOf(uri));
            updatePhotoIndexLabel();
            return;
        }
    }

    private void getGallaryIntent() {
        Dexter.withActivity(getActivity()).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, OPEN_IMAGE_REQUEST_CODE);
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

            }
        }).check();
    }
}
