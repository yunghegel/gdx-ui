package org.yunghegel.gdx.ui.widgets.viewport.events;

import org.yunghegel.gdx.ui.widgets.viewport.ViewportWidget;

public class ViewportResizedEvent extends ViewportEvent{

    int width, height;

    public ViewportResizedEvent(ViewportWidget widget, int width, int height) {
        super(widget);
        this.width = width;
        this.height = height;
    }



}
