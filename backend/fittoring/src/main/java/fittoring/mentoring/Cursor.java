package fittoring.mentoring;

import fittoring.mentoring.business.model.SortKey;

public record Cursor(
        SortKey sortKey,
        String dir,
        long sortValue,
        long id
) {
    public boolean isSameSortKey(SortKey sortKey) {
        return this.sortKey.equals(sortKey);
    }
}
