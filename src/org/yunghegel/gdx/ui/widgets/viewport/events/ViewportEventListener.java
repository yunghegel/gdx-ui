package org.yunghegel.gdx.ui.widgets.viewport.events;

import org.yunghegel.gdx.ui.widgets.viewport.ViewportWidget;

public class ViewportEventListener {

    public boolean handle(ViewportEvent event , ViewportWidget widget){
        if(event instanceof ViewportExitedEvent) {
            System.out.println("handling event " +event.getClass().getSimpleName());

            return exited(event);
        }
        if(event instanceof ViewportEnteredEvent) {
            System.out.println("handling event " +event.getClass().getSimpleName());

            return entered(event);
        }
        if(event instanceof ViewportResizedEvent) {
            return resized(event, ((ViewportResizedEvent) event).width, ((ViewportResizedEvent) event).height);
        }
        if(event instanceof GameViewportAppliedEvent) {
            return gameViewportApplied(event);
        }
        if(event instanceof StageViewportAppliedEvent) {
            return stageViewportApplied(event);
        }
        if(event instanceof ViewportLayoutEvent) {
            return layout(event, ((ViewportLayoutEvent) event).width, ((ViewportLayoutEvent) event).height, ((ViewportLayoutEvent) event).x, ((ViewportLayoutEvent) event).y);
        }

        return false;
    }


    public boolean exited(ViewportEvent event){
        return false;
    }

    public boolean entered(ViewportEvent event){
        return false;
    }

    public boolean resized(ViewportEvent event, int width, int height){
        return false;
    }

    public boolean gameViewportApplied(ViewportEvent event){
        return false;
    }

    public boolean stageViewportApplied(ViewportEvent event){
        return false;
    }
    public boolean layout(ViewportEvent event, float width, float height, float x, float y){
        return false;
    }

}
