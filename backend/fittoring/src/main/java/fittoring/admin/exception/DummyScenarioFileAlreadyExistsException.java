package fittoring.admin.exception;

public class DummyScenarioFileAlreadyExistsException extends RuntimeException {

    public DummyScenarioFileAlreadyExistsException(String scenarioFile) {
        super("이미 같은 이름의 시나리오 파일이 존재합니다: " + scenarioFile);
    }

    public DummyScenarioFileAlreadyExistsException(String scenarioFile, Throwable cause) {
        super("이미 같은 이름의 시나리오 파일이 존재합니다: " + scenarioFile, cause);
    }
}
