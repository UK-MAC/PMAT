package uk.co.awe.pmat.db;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * A class to represent the different type of rank data stored for a value;
 * either a specific numeric rank or the type of rank aggregation that the value
 * holds.
 *
 * @author AWE Plc copyright 2013
 */
public final class Rank implements Comparable<Rank>, Serializable {

    /** The value is the same on all ranks. */
    public static final Rank ALL_RANKS = new Rank(RankEnum.ALL_RANKS);

    /** The value is the same on any ranks. */
    public static final Rank ANY_RANK = new Rank(RankEnum.ANY_RANK);

    /** The value is given as the rank average. */
    public static final Rank RANK_AVG = new Rank(RankEnum.RANK_AVG);

    /** The value is given as the rank maximum. */
    public static final Rank RANK_MAX = new Rank(RankEnum.RANK_MAX);

    /** The value is given as the rank minimum. */
    public static final Rank RANK_MIN = new Rank(RankEnum.RANK_MIN);

    /** The rank of the value is unknown. */
    public static final Rank UNKNOWN = new Rank(RankEnum.UNKNOWN);

    private static final Map<Integer, Rank> RANK_POOL = new HashMap<>();
    private static final Map<RankEnum, Rank> RANK_MAP = new EnumMap<>(RankEnum.class);

    static {
        RANK_MAP.put(RankEnum.ALL_RANKS, ALL_RANKS);
        RANK_MAP.put(RankEnum.ANY_RANK, ANY_RANK);
        RANK_MAP.put(RankEnum.RANK_AVG, RANK_AVG);
        RANK_MAP.put(RankEnum.RANK_MAX, RANK_MAX);
        RANK_MAP.put(RankEnum.RANK_MIN, RANK_MIN);
        RANK_MAP.put(RankEnum.UNKNOWN, UNKNOWN);
    }

    /**
     * Helper {@code Enum} constants for the special database rank values.
     */
    private enum RankEnum {
        ALL_RANKS(-1, "All Ranks"),
        ANY_RANK(-2, "Any Rank"),
        RANK_AVG(-3, "Rank Average"),
        RANK_MAX(-4, "Rank Maximum"),
        RANK_MIN(-5, "Rank Minimum"),
        UNKNOWN(-6, "Unknown");

        private int rank;
        private String displayName;

        /**
         * Create a new {@code RankEnum}.
         *
         * @param rank the database integer rank.
         * @param displayName the name to display for this rank.
         */
        private RankEnum(int rank, String displayName) {
            this.rank = rank;
            this.displayName = displayName;
        }

        /**
         * Convert a string representation of a rank into the corresponding
         * {@code Enum}.
         *
         * @param rank the rank as a string.
         * @return the rank as an {@code Enum}.
         */
        static RankEnum fromString(String rank) {
            for (RankEnum rankEnum : values()) {
                if (rankEnum.displayName.equals(rank)) { return rankEnum; }
            }
            return null;
        }

        /**
         * Convert an integer representation of a rank into the corresponding
         * {@code Enum}.
         *
         * @param rank the rank as an integer.
         * @return the rank as an {@code Enum}.
         */
        static RankEnum fromInteger(int rank) {
            for (RankEnum rankEnum : values()) {
                if (rankEnum.rank == rank) { return rankEnum; }
            }
            return null;
        }
    }

    private int rank;

    /**
     * Create a new {@code Rank}.
     *
     * @param rank the database integer rank value.
     */
    private Rank(int rank) {
        this.rank = rank;
    }

    /**
     * Create a new {@code Rank}.
     *
     * @param rankEnum an {@code Enum} representing a special rank value.
     */
    private Rank(RankEnum rankEnum) {
       this.rank = rankEnum.rank;
    }

    /**
     * Return the database integer rank representation of this {@code Rank}.
     *
     * @return the rank as an integer.
     */
    public Integer asInteger() {
        return rank;
    }

    /**
     * Return the {@code Rank} object corresponding to the given integer rank.
     *
     * @param rankInt the integer rank.
     * @return the {@code Rank}.
     */
    public static Rank fromInteger(Integer rankInt) {
        Rank rank = RANK_POOL.get(rankInt);
        if (rank == null) {
            if (rankInt == null) {
                rank = Rank.UNKNOWN;
            } else {
                rank = new Rank(rankInt);
            }
            RANK_POOL.put(rankInt, rank);
        }
        return rank;
    }

    /**
     * Return the {@code Rank} object corresponding the name of the rank.
     *
     * @param rankString the rank string.
     * @return the {@code Rank}.
     */
    public static Rank fromString(String rankString) {
        RankEnum rankEnum = RankEnum.fromString(rankString);
        if (rankEnum != null) {
            return RANK_MAP.get(rankEnum);
        } else {
            return fromInteger(Integer.parseInt(rankString));
        }
    }

    /**
     * Return the {@code Rank} object corresponding the given string
     * representation of the rank.
     *
     * @param rankString the rank string.
     * @return the {@code Rank}.
     */
    public static Rank valueOf(String rankString) {
        try {
            RankEnum rankEnum = RankEnum.valueOf(rankString);
            return RANK_MAP.get(rankEnum);
        } catch (IllegalArgumentException ex) {
            return fromInteger(Integer.parseInt(rankString));
        }
    }

    /**
     * Return the name of the rank.
     *
     * @return the rank name.
     */
    public String name() {
        if (rank < 0) {
            return RankEnum.fromInteger(rank).name();
        } else {
            return String.format("%d", rank);
        }
    }
    
    @Override
    public String toString() {
        if (rank < 0) {
            return RankEnum.fromInteger(rank).displayName;
        } else {
            return String.format("%d", rank);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Rank)) {
            return false;
        }
        final Rank other = (Rank) obj;
        if (this.rank != other.rank) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + this.rank;
        return hash;
    }

    @Override
    public int compareTo(Rank other) {
        return rank - other.rank;
    }

}
