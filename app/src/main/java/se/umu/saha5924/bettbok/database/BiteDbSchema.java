package se.umu.saha5924.bettbok.database;

/**
 * BiteDbSchema is responsible for the schema used by the database to store the Bites.
 */
public class BiteDbSchema {

    public static final class BiteTable {
        public static final String NAME = "bites";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String PLACEMENT = "placement";
            public static final String CALENDAR = "calendar";
            public static final String STAGE = "stage";
        }
    }
}
