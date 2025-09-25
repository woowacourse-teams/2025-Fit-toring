package fittoring.mentoring.presentation.dto;

import java.util.List;

public record SliceResponse<T>(
        List<T> content,
        boolean hasNext,
        String nextCursor
) {}
