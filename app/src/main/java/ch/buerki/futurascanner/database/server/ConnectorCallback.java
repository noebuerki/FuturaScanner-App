package ch.buerki.futurascanner.database.server;

public interface ConnectorCallback {
    void onSuccess();

    void onFail();
}
