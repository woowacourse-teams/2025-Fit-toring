package fittoring.admin.presentation.dto;

public record DummySqlInsertStatusResponse(
        int fileSeq,
        String scenarioFile,
        boolean inserted
) {
}
