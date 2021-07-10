package ch.buerki.futurascanner.ui.scan.helpers.scanner;

import android.graphics.Bitmap;

import com.google.mlkit.vision.barcode.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

public class Analyzer {

    private final BarcodeScannerOptions options;

    public Analyzer() {
        options = new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_EAN_13, Barcode.FORMAT_CODE_128)
                .build();
    }

    public void analyze(Bitmap bitmap, AnalyzerCallback callback) {
        InputImage image =
                InputImage.fromBitmap(bitmap, 0);

        BarcodeScanner scanner = BarcodeScanning.getClient(options);

        scanner.process(image)
                .addOnSuccessListener(barcodes -> {
                    if (barcodes.size() == 1) {
                        callback.onSuccess(barcodes.get(0));
                    }
                });
    }
}
