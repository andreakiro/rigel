package rigel.astronomy.objects;

import static rigel.Preconditions.checkArgument;

import java.util.List;

public final class Asterism {
    private final List<Star> stars;

    public Asterism(List<Star> stars) {
        checkArgument(!stars.isEmpty());
        this.stars = List.copyOf(stars);
    }

    public List<Star> stars() {
        return stars;
    }
}