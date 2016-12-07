package course.examples.creaturun;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class GetCreatures extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();   // Hide the title bar
        setContentView(R.layout.activity_get_creatures);
    }
}
