package fittoring.application.community.repository;

import fittoring.application.community.service.dto.PostPaginationResult;
import fittoring.util.Cursor;

public interface CustomPostRepository {

    PostPaginationResult findPostsWithPagination(Cursor cursor);
}
