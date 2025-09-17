package fittoring.mentoring;

import fittoring.mentoring.business.model.SortKey;

public record Cursor(
        SortKey sortKey,
        String dir,
        long sortValue,
        long id
) {
}
