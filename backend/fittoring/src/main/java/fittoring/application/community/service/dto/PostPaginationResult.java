package fittoring.application.community.service.dto;

import fittoring.domain.model.Post;
import java.util.List;

public record PostPaginationResult(
        List<Post> posts,
        String nextCursorCode,
        boolean hasNext
) {
}
