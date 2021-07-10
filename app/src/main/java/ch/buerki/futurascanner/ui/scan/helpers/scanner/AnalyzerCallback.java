package ch.buerki.futurascanner.ui.scan.helpers.scanner;

import com.google.mlkit.vision.barcode.Barcode;

public interface AnalyzerCallback {
    void onSuccess(Barcode barcode);
}
