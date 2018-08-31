package com.joekoperski.joemino;

import android.content.Context;
import android.graphics.Point;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

class SizedImageButton extends ImageButton {
    SizedImageButton( Context context, int resource, Point size, Point position ){
        super( context );

        RelativeLayout.LayoutParams layoutButtonNew = new RelativeLayout.LayoutParams( WRAP_CONTENT, WRAP_CONTENT );
        setImageResource(resource);
        setBackgroundColor( 0 ); // transparent

        layoutButtonNew.width = size.x;
        layoutButtonNew.height = size.y;
        layoutButtonNew.setMargins(position.x, position.y, 0, 0);

        setPadding(0,0,0,0);
        setLayoutParams(layoutButtonNew);
        setScaleType( ImageView.ScaleType.FIT_XY );
    }
}// SizedImageButton
