package uk.co.awe.pmat.db;

public interface DatabaseSetup {
    void setup();
    void closeDown();
    void setupClass();
}
