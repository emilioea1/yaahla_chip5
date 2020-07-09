package com.chip.parkpro1.ui.activities;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.chip.parkpro1.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

public class GeneratedCodeActivity extends AppCompatActivity {

    ImageView codeView;
    Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generated_code);

        codeView = findViewById(R.id.generated_code);
        submitButton = findViewById(R.id.submit_button);

        try {
            codeView.setImageBitmap(createBarcode("478785551485"));
        } catch (WriterException e) {
            e.printStackTrace();
        }

    }

    Bitmap createBarcode(String data) throws WriterException {
        int size = 500;
        MultiFormatWriter barcodeWriter = new MultiFormatWriter();

        BitMatrix barcodeBitMatrix = barcodeWriter.encode(data, BarcodeFormat.QR_CODE, size, size);
        Bitmap barcodeBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                barcodeBitmap.setPixel(x, y, barcodeBitMatrix.get(x, y) ?
                        Color.BLACK : Color.TRANSPARENT);
            }
        }
        return barcodeBitmap;
    }
}
