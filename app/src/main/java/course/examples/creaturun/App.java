package course.examples.creaturun;

import android.app.Application;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by maria on 12/10/2016.
 */

public class App extends Application {

    @Override
    public void onCreate() { super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Vanilla.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }
}
