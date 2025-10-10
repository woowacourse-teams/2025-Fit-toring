package fittoring.admin.presentation.dto;

import java.util.List;

public record PageResult<T>(
    List<T> content,
    int page,
    int size,
    long total,
    int totalPages,
    boolean hasNext
) {
}
