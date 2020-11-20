package com.example.camtest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.material.textfield.TextInputEditText;

import org.apache.commons.lang3.ArrayUtils;

public class CameraActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        Log.i(null, "Starting camera");

        // Setup spinner
        Spinner spinner = findViewById(R.id.spinner_id);
        String[] cameraIds = getCameraIds();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, cameraIds);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new ItemSelectedHandler());
    }


    private String[] getCameraIds() {
        Context ctx = super.getApplicationContext();
        CameraManager cameraManager = (CameraManager) ctx.getSystemService(CAMERA_SERVICE);
        try {
            String[] ids = cameraManager.getCameraIdList();
            for (String id : ids) {
                Log.i(null, "FOUND CAM " + id);
            }
            return ids;
        } catch (CameraAccessException e) {
            Log.e(null, e.getMessage());
            return new String[0];
        }
    }

    private void setInfoText(String info) {
        TextInputEditText text = findViewById(R.id.text_characteristics);
        text.setText(info);
    }

    private String constructCameraInfoText(String cameraId) throws IllegalArgumentException {
        Context ctx = super.getApplicationContext();
        CameraManager cameraManager = (CameraManager) ctx.getSystemService(CAMERA_SERVICE);
        try {
            StringBuilder str = new StringBuilder();
            CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
            for (CameraCharacteristics.Key<?> key: characteristics.getKeys()) {
                Object value = characteristics.get(key);
                str.append(key.getName()).append(" : ").append(value.toString()).append("\n");
            }
            return str.toString();
        } catch (CameraAccessException exception) {
            return "CAMERA ACCESS EXCEPTION: " + exception.getMessage();
        }

    }

    private class ItemSelectedHandler implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            setInfoText("Item selected");
            String cameraId = (String) parent.getItemAtPosition(position);
            Log.i(null, "SELECTED " + cameraId);
            setInfoText(constructCameraInfoText(cameraId));
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            setInfoText("Please select camera ID");
        }
    }
}