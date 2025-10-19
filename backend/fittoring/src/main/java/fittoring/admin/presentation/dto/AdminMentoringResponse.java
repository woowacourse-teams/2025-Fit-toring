package fittoring.admin.presentation.dto;

import java.util.List;

public record AdminMentoringResponse(
        Long mentoringId,
        String mentorName,
        List<String> categories,
        int price
) {
}
