package fittoring.application.community.dummy;

public record DummySqlInsertResponse(
        int fileSeq,
        String scenarioFile,
        int insertedScenarioCount,
        int insertedPostPendingCount,
        int insertedCommentPendingCount,
        String status
) {
}
