package fittoring.util;

import fittoring.mentoring.Cursor;
import fittoring.mentoring.business.exception.InvalidCursorException;

public class CursorCodec {
    public static Cursor decode(String cursorCode) {
        if (cursorCode == null || cursorCode.isBlank()) {
            return null;
        }
        String[] p = cursorCode.split("\\|");
        if (p.length != 4) {
            throw new InvalidCursorException("Invalid cursor");
        }
        return new Cursor(Long.parseLong(p[2]), Integer.parseInt(p[3]));
    }

    public static String incode(Cursor cursor) {
        return String.join("|",
                Long.toString(cursor.sortValue()), Long.toString(cursor.id()));
    }
}
