package org.yunghegel.gdx.ui.widgets.viewport;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Queue;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import net.dermetfan.gdx.scenes.scene2d.ui.UIUtils;
import org.yunghegel.gdx.ui.widgets.viewport.events.*;

import java.util.Stack;

public class ViewportWidget extends Widget {

    public ScreenViewport viewport;
    public Stage stage;
    public ViewportStage uiStage;

    public Vector2 temp = new Vector2();
    public Vector2 origin = new Vector2();
    public int width;
    public int height;
    public int viewportOriginalX =0, viewportOriginalY=0;

    public boolean active = false;
    public boolean previousActive = false;

    private Renderer renderer;
    private InputMultiplexer inputs;
    private InputProcessor viewportInputs;

    Queue<ViewportEvent> viewportEvents = new Queue<>();
    Array<ViewportEventListener> viewportEventListeners = new Array<>();




    public ViewportWidget(ScreenViewport viewport, Stage stage) {
         this.viewport = viewport;
        this.stage = stage;
        uiStage = new ViewportStage(this);

        viewportInputs = inputs;

        createListeners();
    }

    private void createListeners(){
        addListener(new InputListener() {

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                getStage().setKeyboardFocus(ViewportWidget.this);
                getStage().setScrollFocus(ViewportWidget.this);
                stage.setKeyboardFocus(ViewportWidget.this);
                stage.setScrollFocus(ViewportWidget.this);

                super.enter(event, x, y, pointer, fromActor);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                stage.setKeyboardFocus(null);
                stage.setScrollFocus(null);

                super.exit(event, x, y, pointer, toActor);
            }
        });

        stage.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if(stage.getScrollFocus()==ViewportWidget.this){
                    stage.setScrollFocus(null);
                }
                if(stage.getKeyboardFocus()==ViewportWidget.this){
                    stage.setKeyboardFocus(null);
                }
                return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                return super.mouseMoved(event, x, y);
            }


        });

    }

    @Override
    public void act(float delta) {
        temp.set(0, 0);
        localToScreenCoordinates(temp);
        if (viewport == null) return;
        viewport.setScreenPosition(viewportOriginalX + MathUtils.round(temp.x), viewportOriginalY + MathUtils.round(Gdx.graphics.getHeight() - temp.y));
        boolean widgetActive = isInsideWidget(Gdx.input.getX(),Gdx.graphics.getHeight()- Gdx.input.getY());
        if(widgetActive!=active){
            if(widgetActive){
                handleEvent(new ViewportEnteredEvent(this));

            } else {
                handleEvent(new ViewportExitedEvent(this));

            }
            active=widgetActive;
        }


    }

    @Override
    public void layout() {
        temp.set(0, 0);
        localToScreenCoordinates(temp);
        if (viewport == null) return;
        viewport.update(MathUtils.round(getWidth()), MathUtils.round(getHeight()));
        viewportOriginalX = viewport.getScreenX();
        viewportOriginalY = viewport.getScreenY();
        handleEvent(new ViewportLayoutEvent(this,viewportOriginalX, viewportOriginalY,temp.x,temp.y));
    }

    @Override
    public void draw(Batch batch , float parentAlpha) {

        batch.end();

        if (viewport != null) {
            calculateBounds();
            viewport.setScreenBounds((int) origin.x , (int) origin.y , width , height);
            viewport.setWorldSize(width * viewport.getUnitsPerPixel() , (height) * viewport.getUnitsPerPixel());
            viewport.apply();
            handleEvent(new GameViewportAppliedEvent(this));
        }



        if(renderer!=null){
            renderer.render(Gdx.graphics.getDeltaTime());
        }

        stage.getViewport().apply();
        uiStage.act();
        uiStage.getViewport().apply();
        uiStage.draw();


        handleEvent(new StageViewportAppliedEvent(this));
        batch.begin();
    }

    public void setViewportInputProcessor(InputProcessor inputProcessor){
        viewportInputs=inputProcessor;
    }

    private void calculateBounds(){
        origin.set(getOriginX() , getOriginY());
        origin = localToStageCoordinates(origin);
        width = (int) getWidth();
        height = (int) getHeight();
    }

    public Rectangle getBoundingRectangle(){
        Vector2 viewportOrigin = new Vector2(0,0);
        localToScreenCoordinates(viewportOrigin);
        viewportOrigin.y = Gdx.graphics.getHeight() - viewportOrigin.y;
        float width = getWidth();
        float height = getHeight();
        return new Rectangle(viewportOrigin.x, viewportOrigin.y, width, height);
    }

    public boolean isInsideWidget(float x, float y) {
      return getBoundingRectangle().contains(x, y);
    }

    private void handleEvent(ViewportEvent event){
        for(ViewportEventListener listener: viewportEventListeners){
            listener.handle(event, this);
        }
    }

    public void addViewportListener(ViewportEventListener listener){
        viewportEventListeners.add(listener);
    }


    public void setRenderer (Renderer renderer) {
        this.renderer = renderer;
    }

    public interface Renderer {
        void render(float delta);
    }

}
