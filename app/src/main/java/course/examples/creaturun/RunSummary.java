package course.examples.creaturun;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;

/**
 * Displays a summery of the user's run.
 */
public class RunSummary extends AppCompatActivity {

    ImageView mapView;
    TextView totalDistanceView;
    TextView totalTimeView;
    TextView averageSpeedView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_summary);

        mapView = (ImageView)findViewById(R.id.map_view);
        totalDistanceView = (TextView) findViewById(R.id.summaryTotalDistanceView);
        totalTimeView = (TextView) findViewById(R.id.summaryTotalTimeView);
        averageSpeedView = (TextView) findViewById(R.id.summaryAverageSpeedView);

        Intent intent = getIntent();
        byte[] bytearray = intent.getByteArrayExtra(RunningActivity2.BITMAP_KEY);
        Bitmap bmp = BitmapFactory.decodeByteArray(bytearray,0,bytearray.length);
        mapView.setImageBitmap(bmp);

        totalDistanceView.setText("Total distance: "
         + new DecimalFormat("#.##").format(intent.getDoubleExtra(RunningActivity2.DISTANCE_KEY,0) * 0.000621371) + " miles");
        totalTimeView.setText("Total time: " + intent.getStringExtra(RunningActivity2.ELAPSED_TIME_KEY));
        averageSpeedView.setText("Average speed: " +
                        String.valueOf(new DecimalFormat("#.##").format( intent.getDoubleExtra(RunningActivity2.SPEED_KEY,0))) + " mph");
    }

    /*
        Start the GetCreatures activity.
     */
    public void onClickGetCreatures(View v) {
        Intent intent = new Intent(this, GetCreatures.class);
        startActivity(intent);
    }
}
