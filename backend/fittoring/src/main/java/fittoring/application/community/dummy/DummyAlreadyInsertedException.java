package fittoring.application.community.dummy;

public class DummyAlreadyInsertedException extends RuntimeException {

    public DummyAlreadyInsertedException(String scenarioFile) {
        super("이미 적재된 시나리오 파일입니다: " + scenarioFile);
    }

    public DummyAlreadyInsertedException(String scenarioFile, Throwable cause) {
        super("이미 적재된 시나리오 파일입니다: " + scenarioFile, cause);
    }
}
