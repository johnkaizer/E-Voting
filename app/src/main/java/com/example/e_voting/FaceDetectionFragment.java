package com.example.e_voting;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.e_voting.Helper.GraphicOverlay;
import com.example.e_voting.Helper.RectOverlay;
import com.example.e_voting.databinding.FragmentFaceDetectionBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.wonderkiln.camerakit.CameraKit;
import com.wonderkiln.camerakit.CameraListener;
import com.wonderkiln.camerakit.CameraView;

import java.io.File;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class FaceDetectionFragment extends Fragment {
    private FragmentFaceDetectionBinding binding;
    private Button faceDetectBtn;
    private GraphicOverlay graphicOverlay;
    private CameraView cameraView;
    AlertDialog alertDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentFaceDetectionBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        faceDetectBtn = root.findViewById(R.id.detect_face_btn);
        graphicOverlay = root.findViewById(R.id.graphic_overlay);
        cameraView = root.findViewById(R.id.camera_view);
        cameraView.setFacing(CameraKit.Constants.FACING_FRONT);
        alertDialog = new AlertDialog.Builder(getContext())
                .setMessage("Please wait, Processing image...")
                .setCancelable(true)
                .create();

        faceDetectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.start();
                cameraView.captureImage();
                graphicOverlay.clear();
            }
        });
        cameraView.setCameraListener(new CameraListener() {

            @Override
            public void onCameraOpened() {
                super.onCameraOpened();
            }

            @Override
            public void onCameraClosed() {
                super.onCameraClosed();
            }

            @Override
            public void onPictureTaken(byte[] picture) {
                alertDialog.show();

                // Create a bitmap
                Bitmap result = BitmapFactory.decodeByteArray(picture, 0, picture.length);
                processFaceDetection(result);
            }

            @Override
            public void onVideoTaken(File video) {
                super.onVideoTaken(video);
            }

        });


        return root;
    }

    private void processFaceDetection(Bitmap result) {
        FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(result);
        FirebaseVisionFaceDetectorOptions firebaseVisionFaceDetectorOptions =
                new FirebaseVisionFaceDetectorOptions.Builder().build();
        FirebaseVisionFaceDetector firebaseVisionFaceDetector = FirebaseVision.getInstance()
                .getVisionFaceDetector(firebaseVisionFaceDetectorOptions);

        firebaseVisionFaceDetector.detectInImage(firebaseVisionImage)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionFace> firebaseVisionFaces) {
                        getFaceResults(firebaseVisionFaces);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Error: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getFaceResults(List<FirebaseVisionFace> firebaseVisionFaces) {
        int counter = 0;
        for (FirebaseVisionFace face : firebaseVisionFaces) {
            Rect rect = face.getBoundingBox();
            RectOverlay rectOverlay = new RectOverlay(graphicOverlay, rect);

            graphicOverlay.add(rectOverlay);
            counter++;
        }

        // Dismiss the alertDialog on the main UI thread
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                alertDialog.dismiss();

                // Perform UI-related tasks
                Toast.makeText(getContext(), "Voter verified successfully", Toast.LENGTH_SHORT).show();

                // Finish the fragment
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .remove(FaceDetectionFragment.this).commit();

                // Stop the camera after all operations are complete
                cameraView.stop();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        cameraView.stop();
    }

    @Override
    public void onResume() {
        super.onResume();
        cameraView.start();
    }
}
