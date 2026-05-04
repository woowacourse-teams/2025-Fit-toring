package fittoring.application.community.dummy;

public class DummyScenarioFileNotFoundException extends RuntimeException {

    public DummyScenarioFileNotFoundException(String fileName) {
        super("시나리오 파일을 찾을 수 없습니다: " + fileName);
    }
}
