package org.yunghegel.gdx.ui.widgets.viewport.events;

import org.yunghegel.gdx.ui.widgets.viewport.ViewportWidget;

public class ViewportLayoutEvent extends ViewportEvent{

        public float width;
        public float height;
        public float x;
        public float y;


        public ViewportLayoutEvent(ViewportWidget widget,float width,float height,float x,float y) {
            super(widget);
            this.width = width;
            this.height = height;
            this.x = x;
            this.y = y;

        }
}
