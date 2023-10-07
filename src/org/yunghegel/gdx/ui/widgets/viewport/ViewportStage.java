package org.yunghegel.gdx.ui.widgets.viewport;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import org.yunghegel.gdx.ui.widgets.STable;

public class ViewportStage extends Stage {

    private STable root;
    private Matrix4 projectionMatrix;
    private ViewportProperties viewportProperties;
    private ScreenViewport viewport;



    private class ViewportProperties {

        private Vector2 temp = new Vector2();
        private Vector2 origin = new Vector2();
        private int width;
        private int height;
        private int viewportOriginalX =0, viewportOriginalY=0;

        private final ScreenViewport viewport;

        ViewportProperties(ScreenViewport viewport) {
            this.viewport = viewport;
        }

        public void update(){
            viewportOriginalX = viewport.getScreenX();
            viewportOriginalY = viewport.getScreenY();
            width = viewport.getScreenWidth();
            height = viewport.getScreenHeight();
            origin.set(width / 2, height / 2);
            temp.set(origin.x, origin.y);
            viewport.unproject(temp);
            origin.set(temp.x, temp.y);
        }

        public Matrix4 calculateProjectionMatrix() {
            projectionMatrix.setToOrtho2D(viewportOriginalX, viewportOriginalY, width, height);
            projectionMatrix.translate(origin.x, origin.y, 0);
            projectionMatrix.scale(1, -1, 1);
            return projectionMatrix;
        }

    }


    public ViewportStage(ViewportWidget widget) {
        super(new ScreenViewport());
        viewport = (ScreenViewport) getViewport();
        root = new STable();
        root.setFillParent(true);
        addActor(root);
        viewportProperties = new ViewportProperties(widget.viewport);
    }


    @Override
    public void act() {
        super.act();
        viewportProperties.update();
        getViewport().setScreenPosition(viewportProperties.viewportOriginalX, viewportProperties.viewportOriginalY);
        getViewport().update(viewportProperties.width, viewportProperties.height, true);
    }

}
