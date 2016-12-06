package course.examples.creaturun;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;

// adapted from: https://developer.android.com/guide/topics/ui/layout/gridview.html
public class CreatureView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Collected Creatures");
        setContentView(R.layout.activity_creature_view);

        GridView gridview = (GridView) findViewById(R.id.creatureGrid);
        gridview.setAdapter(new ImageAdapter(this));
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RelativeLayout rl = (RelativeLayout) findViewById(R.id.activity_creature_view);
                final Creature[] creatures = new Creature[] {
                        new Creature(Creature.CreatureType.CAT),
                        new Creature(Creature.CreatureType.FISH),
                        new Creature(Creature.CreatureType.PIG),
                        new Creature(Creature.CreatureType.PUMPKIN),
                        new Creature(Creature.CreatureType.SANIC),
                        new Creature(Creature.CreatureType.SNAIL)};
                CreaturePopupWindow popup = new CreaturePopupWindow(creatures[position], rl.getContext());
                popup.showPopup();
            }
        });
    }

    // method for going back to the title screen from creature view
    public void onBack(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
