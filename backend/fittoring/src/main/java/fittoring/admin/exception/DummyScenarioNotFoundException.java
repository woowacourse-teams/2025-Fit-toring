package fittoring.admin.exception;

public class DummyScenarioNotFoundException extends RuntimeException {

    public DummyScenarioNotFoundException(long scenarioId) {
        super("더미 시나리오를 찾을 수 없습니다: " + scenarioId);
    }
}
