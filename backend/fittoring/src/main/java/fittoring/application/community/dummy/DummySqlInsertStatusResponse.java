package fittoring.application.community.dummy;

public record DummySqlInsertStatusResponse(
        int fileSeq,
        String scenarioFile,
        boolean inserted
) {
}
