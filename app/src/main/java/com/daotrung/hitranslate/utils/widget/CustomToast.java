package com.daotrung.hitranslate.utils.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daotrung.hitranslate.R;

public class CustomToast extends Toast {

    public static int SUCCESS = 1;
    public static int WARNING = 2;
    public static int ERROR = 3;
    public static int CONFUSING = 4;

    private static long SHORT = 4000;
    private static long LONG = 7000;

    public CustomToast(Context context) {
        super(context);
    }

    public static Toast makeText(Context context, String message, int duration, int type, boolean androidicon) {
        Toast toast = new Toast(context);
        toast.setDuration(duration);
        View layout = LayoutInflater.from(context).inflate(R.layout.custom_toast_layout, null, false);
        TextView l1 = (TextView) layout.findViewById(R.id.toast_text);
        LinearLayout linearLayout = (LinearLayout) layout.findViewById(R.id.toast_type);
        ImageView img = (ImageView) layout.findViewById(R.id.toast_icon);
        l1.setText(message);
        if (type == 1) {
            linearLayout.setBackgroundResource(R.drawable.success_shape);
            img.setImageResource(R.drawable.ic_baseline_done_24);
        } else if (type == 2) {
            linearLayout.setBackgroundResource(R.drawable.warning_shape);
            img.setImageResource(R.drawable.ic_baseline_pan_tool_24);
        } else if (type == 3) {
            linearLayout.setBackgroundResource(R.drawable.error_shape);
            img.setImageResource(R.drawable.ic_baseline_clear_24);
        } else if (type == 4) {
            linearLayout.setBackgroundResource(R.drawable.confusing_shape);
            img.setImageResource(R.drawable.ic_baseline_refresh_24);
        }
        toast.setView(layout);
        return toast;
    }

    public static Toast makeText(Context context, String message, int duration, int type, int ImageResource) {
        Toast toast = new Toast(context);
        View layout = LayoutInflater.from(context).inflate(R.layout.custom_toast_layout, null, false);
        TextView l1 = (TextView) layout.findViewById(R.id.toast_text);
        LinearLayout linearLayout = (LinearLayout) layout.findViewById(R.id.toast_type);
        ImageView img = (ImageView) layout.findViewById(R.id.toast_icon);
        l1.setText(message);
        if (type == 1) {
            linearLayout.setBackgroundResource(R.drawable.success_shape);
            img.setImageResource(ImageResource);
        } else if (type == 2) {
            linearLayout.setBackgroundResource(R.drawable.warning_shape);
            img.setImageResource(ImageResource);
        } else if (type == 3) {
            linearLayout.setBackgroundResource(R.drawable.error_shape);
            img.setImageResource(ImageResource);
        } else if (type == 4) {
            linearLayout.setBackgroundResource(R.drawable.confusing_shape);
            img.setImageResource(ImageResource);
        }
        toast.setView(layout);
        return toast;
    }
}
