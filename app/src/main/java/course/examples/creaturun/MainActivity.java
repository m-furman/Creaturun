package course.examples.creaturun;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //This is just temporary, you can change the layout or anything, just make sure there's still a way to get to GetCreatures
    public void onGetCreatures(View view) {
        Intent intent = new Intent(this, GetCreatures.class);
        startActivity(intent);
    }

    // method for accessing the creature view screen
    public void onViewCreatures(View view) {
        Intent intent = new Intent(this, CreatureView.class);
        startActivity(intent);
    }

    /*
        Method that launches the running activity.
     */
    public void onStartRun(View view) {
        Intent intent = new Intent(this, RunningActivity.class);
        startActivity(intent);
    }
}
