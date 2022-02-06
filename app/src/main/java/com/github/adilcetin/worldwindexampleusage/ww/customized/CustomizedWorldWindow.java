package com.github.adilcetin.worldwindexampleusage.ww.customized;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Choreographer;

import gov.nasa.worldwind.WorldWindow;

public class CustomizedWorldWindow extends WorldWindow {

    public CustomizedWorldWindow(Context context) {
        super(context);
    }

    public CustomizedWorldWindow(Context context, EGLConfigChooser configChooser) {
        super(context, configChooser);
    }

    public CustomizedWorldWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Called when the activity is paused. Calling this method will pause the rendering thread, cause any outstanding
     * pick operations to return an empty pick list, and prevent subsequent calls to pick and requestRedraw to return
     * without performing any action.
     */
    @Override
    public void onPause() {
        // Do nothing
        // acetin: Memory Leak
    }

    /**
     * Called when the activity is resumed. Calling this method will resume the rendering thread and enable subsequent
     * calls to pick and requestRedraw to function normally.
     */
    @Override
    public void onResume() {
        // Do nothing
        // acetin: Memory Leak
    }

    @Override
    /**
     * Resets this WorldWindow to its initial internal state.
     */
    protected void reset() {
        // Do nothing
        // acetin: Memory Leak
    }

    public void controlledReset(){
        // Reset any state associated with navigator events.
        this.navigatorEvents.reset();

        // Clear the render resource cache; it's entries are now invalid.
        this.renderResourceCache.clear();

        // Clear the viewport dimensions.
        this.viewport.setEmpty();

        // Clear the frame queue and recycle pending frames back into the frame pool.
        this.clearFrameQueue();

        // Cancel any outstanding request redraw messages.
        Choreographer.getInstance().removeFrameCallback(this);
        this.mainThreadHandler.removeMessages(MSG_ID_REQUEST_REDRAW /*msg.what*/);
        this.isWaitingForRedraw = false;
    }

}
