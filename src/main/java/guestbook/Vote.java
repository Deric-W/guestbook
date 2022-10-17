package guestbook;

/**
 * A enum to represent votings for the rating of {@link GuestbookEntry}s
 *
 * @author Eric Wolf
 */
public enum Vote {
    UP, DOWN;

    /**
     * Calculate the rating delta
     * 
     * @return amount to add to the current rating
     */
    public int getRating() {
        switch (this) {
            case UP:
                return 1;
            case DOWN:
                return -1;
            default:
                throw new RuntimeException("unreachable");
        }
    }
}
