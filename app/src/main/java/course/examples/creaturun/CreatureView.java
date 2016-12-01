package course.examples.creaturun;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

// adapted from: https://developer.android.com/guide/topics/ui/layout/gridview.html
public class CreatureView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creature_view);

        GridView gridview = (GridView) findViewById(R.id.creatureGrid);
        gridview.setAdapter(new ImageAdapter(this));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(CreatureView.this, "" + position,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // method for going back to the title screen from creature view
    public void onBack(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
