package personal.smartms.Entity;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by karan on 31/5/15.
 */
public class MyViewPager extends ViewPager {
    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x,
                                int y) {
        if (v instanceof MyRecyclerView) {
            if(dx<0)
                return(super.canScroll(v, checkV, dx, x, y));
            else
            return(true);
        }

        return(super.canScroll(v, checkV, dx, x, y));
    }
}