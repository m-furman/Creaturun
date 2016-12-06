package course.examples.creaturun;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();   // Hide the title bar
        setContentView(R.layout.activity_main);
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
        Intent intent = new Intent(this, RunningActivity2.class);
        startActivity(intent);
    }
}
