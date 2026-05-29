package fittoring.admin.exception;

public class DummyScenarioDeletionNotAllowedException extends RuntimeException {

    public DummyScenarioDeletionNotAllowedException(long scenarioId) {
        super("적재된 더미 시나리오는 삭제할 수 없습니다: " + scenarioId);
    }
}
