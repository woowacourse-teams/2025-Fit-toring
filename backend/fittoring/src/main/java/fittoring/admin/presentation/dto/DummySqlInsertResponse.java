package fittoring.admin.presentation.dto;

public record DummySqlInsertResponse(
        int fileSeq,
        String scenarioFile,
        int insertedScenarioCount,
        int insertedPostPendingCount,
        int insertedCommentPendingCount,
        String status
) {
}
