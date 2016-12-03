package course.examples.creaturun;

import java.util.Date;

/**
 * Created by Jon on 12/3/16.
 */

public class Creature {
    public enum CreatureType {
        CAT,
        PIG,
        FISH,
        PUMPKIN,
        SANIC,
        SNAIL
    }
    private static int[] imageResources = new int[] {
            R.drawable.cat,
            R.drawable.pig,
            R.drawable.fish,
            R.drawable.pumpkin,
            R.drawable.sanic,
            R.drawable.snail
    };
    //todo add more creative names
    private static String[] names = new String[] {
            "Cat",
            "Pig",
            "Fish",
            "Pumpkin",
            "Sanic",
            "Snail"
    };

    private CreatureType type;
    private String name;
    private Date catchDate;

    public Creature (CreatureType type, Date catchDate) {
        this.type = type;
        this.catchDate = catchDate;
        this.name = names[this.type.ordinal()];
    }

    public void setName(String newName) {
        name = newName;
    }

    public String getName() {
        return name;
    }

    public int getImageResource() {
        return imageResources[this.type.ordinal()];
    }
}
