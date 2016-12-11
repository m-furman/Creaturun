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
        SNAIL,
        FLUFFY,
        BLOO,
        SLIME
    }
    private static int[] imageResources = new int[] {
            R.drawable.cat,
            R.drawable.pig,
            R.drawable.fish,
            R.drawable.pumpkin,
            R.drawable.sanic,
            R.drawable.snail,
            R.drawable.running_creature_square,
            R.drawable.blue_cat_thing_square,
            R.drawable.weird_green_blob_square
    };
    //todo add more creative names
    private static String[] names = new String[] {
            "Cat",
            "Pig",
            "Fish",
            "Pumpkin",
            "Sanic",
            "Snail",
            "Fluffy",
            "Bloo",
            "Slime"
    };

    private CreatureType type;
    private String name;
    private Date catchDate;

    public Creature (CreatureType type) {
        this.type = type;
        this.catchDate = new Date(System.currentTimeMillis());;
        this.name = names[this.type.ordinal()];
    }

    public void setName(String newName) {
        name = newName;
    }

    public String getName() {
        return name;
    }

    public Date getCatchDate () {
        return catchDate;
    }

    public int getImageResource() {
        return imageResources[this.type.ordinal()];
    }
}
