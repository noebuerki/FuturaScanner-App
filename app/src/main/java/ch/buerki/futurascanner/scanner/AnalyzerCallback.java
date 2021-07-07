package ch.buerki.futurascanner.scanner;

import com.google.mlkit.vision.barcode.Barcode;

public interface AnalyzerCallback {
    void onSuccess(Barcode barcode);
}
