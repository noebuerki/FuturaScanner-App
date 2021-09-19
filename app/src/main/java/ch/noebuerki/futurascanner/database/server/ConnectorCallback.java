package ch.noebuerki.futurascanner.database.server;

public interface ConnectorCallback {
    void onSuccess();

    void onFail();
}
