package course.examples.creaturun;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by Jon on 12/3/16.
 */

public class CreaturePopupWindow {
    Creature creature;
    Context context;

    public CreaturePopupWindow(Creature creature, Context context) {
        this.creature = creature;
        this.context = context;
    }

    public void showPopup() {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("Creaturun");

        LayoutInflater inflater = (LayoutInflater)context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        View alertLayout = inflater.inflate(R.layout.creature_popup, null);
        alert.setView(alertLayout);

        TextView nameText = (TextView) alertLayout.findViewById(R.id.popupName);
        TextView dateText = (TextView) alertLayout.findViewById(R.id.popupCatchDate);
        ImageView preview = (ImageView) alertLayout.findViewById(R.id.popupCreaturePreview);
        nameText.setText(creature.getName());
        dateText.setText(creature.getCatchDate().toString());
        Bitmap creatureBitmap =
                ((BitmapDrawable) ContextCompat.getDrawable(context, creature.getImageResource())).getBitmap();
        preview.setImageBitmap(creatureBitmap);

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = alert.create();
        dialog.show();
    }


}
