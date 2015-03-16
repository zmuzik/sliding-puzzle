package zmuzik.slidingpuzzle.gfx;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.widget.GridView;

import java.lang.reflect.Field;

public class PictureGrid extends GridView {

    public PictureGrid(Context context) {
        super(context);
    }

    @SuppressLint("NewApi")
    @Override
    public int getColumnWidth() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            return super.getColumnWidth();
        else {
            try {
                Field field = GridView.class.getDeclaredField("mColumnWidth");
                field.setAccessible(true);
                Integer value = (Integer) field.get(this);
                field.setAccessible(false);

                return value.intValue();
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
