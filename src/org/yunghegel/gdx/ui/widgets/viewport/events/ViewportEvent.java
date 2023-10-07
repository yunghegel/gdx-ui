package org.yunghegel.gdx.ui.widgets.viewport.events;

import org.yunghegel.gdx.ui.widgets.viewport.ViewportWidget;

public abstract class ViewportEvent {

    ViewportWidget widget;
    public boolean handled=false;

    public ViewportEvent(ViewportWidget widget){
        this.widget = widget;
    }

    public void handle(ViewportEventListener listener){
        listener.handle(this,widget);
        handled=true;
        System.out.println("handled event of type "+this.getClass().getSimpleName());
    }

}
