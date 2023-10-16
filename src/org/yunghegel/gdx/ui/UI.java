package org.yunghegel.gdx.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisSlider;
import com.kotcrab.vis.ui.widget.VisTable;
import com.ray3k.stripe.FreeTypeSkin;
import org.apache.commons.lang3.SystemUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWNativeWin32;
import org.lwjgl.system.JNI;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.windows.RECT;
import org.lwjgl.system.windows.User32;
import org.lwjgl.system.windows.WINDOWPLACEMENT;
import org.lwjgl.system.windows.WindowProc;
import org.yunghegel.gdx.utils.ui.widgets.ColorBox;
import org.yunghegel.gdx.utils.ui.widgets.Frame;

import javax.swing.*;
import java.nio.DoubleBuffer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class UI {

    public static final String skinClasspath = "skin/uiskin.json";
    public static final String atlasClasspath = "extras/extras.atlas";

    private static FileHandle skinFile;

    private static FreeTypeSkin skin;

    private static TextureAtlas extras;
    private static Array<TextureAtlas.AtlasRegion> regions;
    private static SpriteBatch batch;

    private static BitmapFont font;

    private static boolean loaded = false;

    public static void load() {
        if(loaded) return;
        skinFile = Gdx.files.classpath(skinClasspath);
        skin = new FreeTypeSkin(skinFile);
        extras=new TextureAtlas(Gdx.files.classpath(atlasClasspath));
        regions = extras.getRegions();
        loaded = true;
        font = skin.getFont("default");
        batch = new SpriteBatch();
    }

    public static FreeTypeSkin getSkin() {
        if(!loaded) throw new RuntimeException("UI not loaded!");
        return skin;
    }

    public static BitmapFont getFont() {
        if(!loaded) throw new RuntimeException("UI not loaded!");
        return font;
    }

    public static SpriteBatch getBatch() {
        if(!loaded) throw new RuntimeException("UI not loaded!");
        return batch;
    }

    public static void drawText(String text, float x, float y, Color color) {
        if(!loaded) throw new RuntimeException("UI not loaded!");
        font.setColor(color);
        font.draw(batch, text, x, y);
    }

    public static void drawText(String text, float x, float y) {
        if(!loaded) throw new RuntimeException("UI not loaded!");
        batch.begin();
        font.draw(batch, text, x, y);
        batch.end();
    }



    public static Drawable getDrawable(String name){
        TextureRegion region = new TextureRegion(extras.findRegion(name).getTexture());
        TextureRegionDrawable drawable=new TextureRegionDrawable(region);
        return drawable;
    }

    public static void overwriteWindowProc2() {
        if (!SystemUtils.IS_OS_WINDOWS) return;
        Lwjgl3Graphics g = (Lwjgl3Graphics) Gdx.graphics;
        long lwjglWindow = g.getWindow().getWindowHandle();
        long hwnd = GLFWNativeWin32.glfwGetWin32Window(lwjglWindow);
        long pWindowProc = User32.GetWindowLongPtr(hwnd, User32.GWL_WNDPROC);
        System.out.println("oldptr: " + pWindowProc);
        WindowProc proc = new WindowProc() {
            private final Vector2 tmp = new Vector2();
            private final DoubleBuffer cursorX = BufferUtils.createDoubleBuffer(1);
            private final DoubleBuffer cursorY = BufferUtils.createDoubleBuffer(1);
            private RECT rect;

            private int getX() {
                return MathUtils.floor((float) cursorX.get(0));
            }
            private int getY() {
                return MathUtils.floor((float) cursorY.get(0));
            }

            @Override
            public long invoke(long hwnd, int uMsg, long wParam, long lParam) {
                if (uMsg == User32.WM_NCHITTEST) {
                    try (MemoryStack stack = MemoryStack.stackPush()) {
                        short x = (short) (lParam & 0xFFFF);
                        short y = (short) ((lParam & 0xFFFF0000) >> 16);
                        GLFW.glfwGetCursorPos(GLFW.glfwGetCurrentContext(), cursorX, cursorY);
                        int glfwX = getX();
                        int glfwY = getY();
                        if (rect == null)
                            rect = RECT.calloc(stack);
                        User32.GetWindowRect(hwnd, rect);

                        if (y < rect.top() + 16 && x < rect.left() + 16) {
                            return User32.HTTOPLEFT;
                        }
                        if (y > rect.bottom() - 16 && x > rect.right() - 16) {
                            return User32.HTBOTTOMRIGHT;
                        }
                        if (y < rect.top() + 12 && x > rect.right() - 16) {
                            return User32.HTTOPRIGHT;
                        }
                        if (y > rect.bottom() - 16 && x < rect.left() + 16) {
                            return User32.HTBOTTOMLEFT;
                        }

                        if (y < rect.top() + 8) {
                            return User32.HTTOP;
                        }
                        if (x < rect.left() + 16) {
                            return User32.HTLEFT;
                        }
                        if (y > rect.bottom() - 16) {
                            return User32.HTBOTTOM;
                        }
                        if (x > rect.right() - 16) {
                            return User32.HTRIGHT;
                        }

                        //Test if the pointer is in Title Bar





                        if (getY()<24 && getX()>100&&getX()<Gdx.graphics.getWidth()-100) {
                            return User32.HTCAPTION;
                        }

                        return JNI.callPPPP(hwnd, uMsg, wParam, lParam, pWindowProc);
                    }
                }
                if (uMsg == User32.WM_NCCALCSIZE) {
                    if (wParam == 1) {
                        try (MemoryStack stack = MemoryStack.stackPush()) {
                            WINDOWPLACEMENT windowplacement = WINDOWPLACEMENT.calloc(stack);
                            User32.GetWindowPlacement(hwnd, windowplacement);
                            // ...but instead we're gonna just pretend it's just a RECT struct
                            // the NCCALCSIZE_PARAMS struct conveniently has what we need
                            // at the very start, so we can quietly say it's a RECT struct lol
                            // hacky because LWJGL doesn't include the structs to the aforementioned
                            // struct, nor some of the other structs contained
                            RECT rect = RECT.create(lParam);
//                            if (windowplacement.showCmd() != User32.SW_MAXIMIZE) {
//                                rect.left(rect.left() + 8);
////								rect.top(rect.top() + 0);
//                                rect.right(rect.right() - 8);
//                                rect.bottom(rect.bottom() - 8);
//                            } else {
//                                rect.left(rect.left() + 8);
//                                rect.top(rect.top() + 8);
//                                rect.right(rect.right() - 8);
//                                rect.bottom(rect.bottom() - 8);
//                            }

                            return rect.address();
                        }
                    }
                }
                return JNI.callPPPP(hwnd, uMsg, wParam, lParam, pWindowProc);
            }
        };
        System.out.println("procaddr: " + proc.address());
        System.out.println("setptr: " + User32.SetWindowLongPtr(hwnd, User32.GWL_WNDPROC, proc.address()));
        System.out.println("setwinptr: " + User32.SetWindowPos(hwnd, 0, 0, 0, 0, 0, User32.SWP_NOMOVE | User32.SWP_NOSIZE | User32.SWP_NOZORDER | User32.SWP_FRAMECHANGED));
    }

    public enum ControlScale{
        LIN, LOG
    }
    public static final float DEFAULT_PADDING = 4;

    public static <T extends Actor> T change(T actor, Consumer<ChangeListener.ChangeEvent> handler){
        actor.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                handler.accept(event);
            }
        });
        return actor;
    }
    public static <T> void select(VisSelectBox<T> selectBox, Consumer<T> item) {
        change(selectBox, event->item.accept(selectBox.getSelected()));
    }
    public static <T extends VisSlider> T changeCompleted(T slider, Consumer<ChangeListener.ChangeEvent> handler){
        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(!slider.isDragging()) handler.accept(event);
            }
        });
        return slider;
    }
    public static TextButton toggle(VisTable t, String text, boolean checked, Consumer<Boolean> handler){
        TextButton bt = toggle(t.getSkin(), text, checked, handler);
        t.add(bt).row();
        return bt;
    }

    public static TextButton toggle(Skin skin, String text, boolean checked, Consumer<Boolean> handler){
        CheckBox bt = new CheckBox(text, skin);
        bt.setChecked(checked);
        change(bt, event->handler.accept(bt.isChecked()));
        return bt;
    }
    public static TextButton trig(Skin skin, String text, Runnable handler){
        TextButton bt = new TextButton(text, skin);
        change(bt, event->handler.run());
        return bt;
    }
    public static TextButton primary(Skin skin, String text, Runnable handler){
        TextButton bt = new TextButton(text, skin, "primary");
        change(bt, event->handler.run());
        return bt;
    }

    public static VisTable colored(Cell<? extends Actor> cell, Color color) {
        cell.getActor().setColor(color);
        return (VisTable) cell.getTable();
    }
    public static Actor clicked(Actor a, Consumer<Event> e) {
        a.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                e.accept(event);
            }
        });
        return a;
    }
    public static Actor clickedOnce(Actor a, Consumer<Event> e) {
        a.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                e.accept(event);
                a.removeListener(this);
            }
        });
        return a;
    }
    public static <T> VisSelectBox<T> selector(Skin skin, Array<T> items, Object defaultItem, Function<T, String> labeler, Consumer<T> handler) {
        return selector(skin, items, defaultItem, labeler, handler, false);
    }
    public static <T> VisSelectBox<T> selector(Skin skin, Array<T> items, Object defaultItem, Function<T, String> labeler, Consumer<T> handler, boolean prependIndex) {
        VisSelectBox<T> selectBox = new VisSelectBox<T>(){
            private ObjectMap<T, String> labels = new ObjectMap<T, String>();
            @Override
            protected String toString(T item) {
                String s = labels.get(item);
                if(s == null){
                    s = labeler.apply(item);
                    if(s == null) s = "";
                    if(prependIndex){
                        s = getItems().indexOf(item, true) + " - " + s;
                    }
                    labels.put(item, s);
                }
                return s;
            }
        };
        selectBox.setItems(items);
        if(defaultItem != null) selectBox.setSelected((T)defaultItem);
        change(selectBox, event->handler.accept(selectBox.getSelected()));

        return selectBox;
    }
    public static VisSelectBox<String> selector(Skin skin, String[] items, int defaultItem, Consumer<Integer> handler) {
        VisSelectBox<String> selectBox = new VisSelectBox<String>();
        selectBox.setItems(new Array<String>(items));
        if(defaultItem >= 0) selectBox.setSelectedIndex(defaultItem);
        change(selectBox, event->handler.accept(selectBox.getSelectedIndex()));
        return selectBox;
    }
    public static <T> VisSelectBox<T> selector(Skin skin, T[] items, T defaultItem, Consumer<T> handler) {
        VisSelectBox<T> selectBox = new VisSelectBox<T>();
        selectBox.setItems(new Array<T>(items));
        if(defaultItem != null) selectBox.setSelected(defaultItem);
        change(selectBox, event->handler.accept(selectBox.getSelected()));
        return selectBox;
    }
    public static <T> Actor selector(Skin skin, String label, T[] items, T defaultItem, Consumer<T> handler) {
        VisSelectBox<T> selectBox = new VisSelectBox<T>();
        selectBox.setItems(new Array<T>(items));
        if(defaultItem != null) selectBox.setSelected(defaultItem);
        change(selectBox, event->handler.accept(selectBox.getSelected()));
        VisTable t = new VisTable();
        t.defaults().pad(DEFAULT_PADDING);
        t.add(label);
        t.add(selectBox);
        return t;
    }
    public static VisSelectBox<String> selector(Skin skin, String ...items) {
        VisSelectBox<String> selectBox = new VisSelectBox<String>();
        selectBox.setItems(items);
        return selectBox;
    }
    public static VisSlider slider(float min, float max, float stepSize, boolean vertical, Skin skin, Float value, Consumer<Float> change) {
        return slider(min, max, stepSize, vertical, skin, value, change, null);
    }
    public static VisSlider slider(float min, float max, float stepSize, boolean vertical, Skin skin, Float value, Consumer<Float> change, Consumer<Float> complete) {
        VisSlider slider = new VisSlider(min, max, stepSize, vertical);
        if(value != null) slider.setValue(value);
        if(change != null) change(slider, e->{
            e.stop();
            //e.cancel();
            change.accept(slider.getValue());
        });
        if(complete != null) changeCompleted(slider, e->complete.accept(slider.getValue()));
        return slider;
    }
    public static VisSlider slider(VisTable table, String name, float min, float max, float val, Consumer<Float> callback) {
        return slider(table, name, min, max, val, org.yunghegel.gdx.utils.ui.UI.ControlScale.LIN, callback);
    }
    public static VisSlider sliderTable(VisTable table, String name, float min, float max, float value, Consumer<Float> setter) {
        return sliderTable(table, name, min, max, value, org.yunghegel.gdx.utils.ui.UI.ControlScale.LIN, setter);
    }

    public static VisSlider sliderTable(VisTable table, String name, float min, float max, float value, org.yunghegel.gdx.utils.ui.UI.ControlScale scale, Consumer<Float> setter) {
        float width = 150;
        float stepSize = (max - min) / width;

        float sMin = scale == org.yunghegel.gdx.utils.ui.UI.ControlScale.LOG ? (float)Math.log10(min) : min;
        float sMax = scale == org.yunghegel.gdx.utils.ui.UI.ControlScale.LOG ? (float)Math.log10(max) : max;
        float sVal = scale == org.yunghegel.gdx.utils.ui.UI.ControlScale.LOG ? (float)Math.log10(value) : value;
        float sStep = scale == org.yunghegel.gdx.utils.ui.UI.ControlScale.LOG ? .01f : stepSize;

        Label number = new Label(round(value, sStep), table.getSkin());

        VisSlider slider = slider(sMin, sMax, sStep, false, table.getSkin(), sVal, val->{
            float nVal = scale == org.yunghegel.gdx.utils.ui.UI.ControlScale.LOG ? (float)Math.pow(10, val) : val;
            setter.accept(nVal);
            number.setText(round(nVal, sStep));
        });

        table.add(name).expandX().right();
        table.add(slider).width(width).left();
        table.add(number).width(50).left();

        table.row();

        return slider;
    }
    public static VisSlider slider(VisTable table, String name, float min, float max, float val, org.yunghegel.gdx.utils.ui.UI.ControlScale scale, Consumer<Float> callback) {
        VisTable t = new VisTable();
        t.defaults().pad(2);
        VisSlider slider = sliderTable(t, name, min, max, val, scale, callback);
        table.add(t).fill().left();
        table.row();
        return slider;
    }
    public static VisSlider slideri(VisTable table, String name, int min, int max, int value, Consumer<Integer> callback) {
        float width = 200;
        Label number = new Label(String.valueOf(value), table.getSkin());
        VisSlider slider = slider((float)min, (float)max, 1f, false, table.getSkin(), (float)value, val->{
            int ival = MathUtils.round(val);
            callback.accept(ival);
            number.setText(ival);
        });
        VisTable t = new VisTable();
        t.defaults().pad(2);

        t.add(name).right();
        t.add(slider).width(width);
        t.add(number).width(50);

        table.add(t).fill();
        table.row();

        return slider;
    }
    public static VisSlider sliderTablei(VisTable table, String name, int min, int max, int value, Consumer<Integer> callback) {
        float width = 200;
        Label number = new Label(String.valueOf(value), table.getSkin());
        VisSlider slider = slider((float)min, (float)max, 1f, false, table.getSkin(), (float)value, val->{
            int ival = MathUtils.round(val);
            callback.accept(ival);
            number.setText(ival);
        });

        table.add(name).right();
        table.add(slider).width(width);
        table.add(number).width(50);
        table.row();

        return slider;
    }
    private static String round(float value, float steps){
        int digits = -MathUtils.round((float)Math.log10(steps));
        if(digits == 0){
            return String.valueOf(MathUtils.round(value));
        }
        float factor = (float)Math.pow(10, digits);
        float adj = MathUtils.round(value * factor) / factor;
        return String.valueOf(adj);
    }
    public static void popup(Stage stage, Skin skin, String title, String message) {
        Dialog dialog = new Dialog(title, skin, "dialog");
        Table t =  dialog.getContentTable();
        t.defaults().pad(DEFAULT_PADDING);
        t.add(message).row();
        t.add(trig(skin, "OK", ()->dialog.hide()));
        dialog.show(stage);
    }
    public static void header(VisTable table, String text) {
        table.add(new Label(text, table.getSkin(), "default")).fillX().row();
    }
    public static VisTable table(Skin skin) {
        VisTable t = new VisTable();
        t.defaults().pad(DEFAULT_PADDING);
        return t;
    }
    public static Frame frame(String title, Skin skin) {
        Label label = new Label(title, skin);
        label.setColor(Color.LIGHT_GRAY);
        Frame frame = new Frame(label);
        frame.getContentTable().setSkin(skin);
        frame.getContentTable().defaults().pad(DEFAULT_PADDING);
        return frame;
    }
    public static Frame frameToggle(String title, Skin skin, boolean checked, Consumer<Boolean> callback) {
        boolean collapseMode = true; // TODO option ?
        Frame frame = new Frame(null);
        Actor bt = toggle(VisUI.getSkin(), title, checked, v->{
            callback.accept(v);
            if(collapseMode)
                frame.showContent(v);
            else
                enableRecursive(frame.getContentTable(), v);
        });
        if(collapseMode)
            frame.showContent(checked);
        else
            enableRecursive(frame.getContentTable(), checked);

        frame.getTitleTable().add(bt);
        frame.getContentTable().defaults().pad(DEFAULT_PADDING);
        frame.getContentTable().setSkin(skin);
        return frame;
    }
    public static void enableRecursive(Actor actor, boolean enabled) {
        if(actor instanceof Disableable){
            ((Disableable) actor).setDisabled(!enabled);
        }
        if(actor instanceof Group){
            Group g = (Group)actor;
            for(Actor child : g.getChildren()){
                enableRecursive(child, enabled);
            }
        }
    }
    public static Dialog dialog(Actor content, String title, Skin skin) {
        Dialog dialog = new Dialog(title, skin, "dialog");
        dialog.getContentTable().add(content).row();
        // dialog.getContentVisTable().add(trig(skin, "Close", ()->dialog.hide()));
        dialog.getTitleTable().add(org.yunghegel.gdx.utils.ui.UI.change(new Button(skin), e->dialog.hide())).pad(0).size(16, 16);

        return dialog;
    }
    public static void colorBox(VisTable table, String name, Color colorModel, boolean alpha) {
        VisTable t = table(table.getSkin());
        t.add(name);
        t.add(new ColorBox(name, colorModel, alpha, table.getSkin()));
        table.add(t).left().row();
    }
    public static <T> VisTable editor(Skin skin, ObjectMap<String, T> map, String selected, Supplier<T> factory, Consumer<String> callback) {
        Array<String> items = new Array<String>();
        for(ObjectMap.Entry<String, T> e : map.entries()){
            items.add(e.key);
        }
        items.sort();
        VisSelectBox<String> selector = selector(skin, items, selected, item->item, item->{
            if(map.size > 0){
                callback.accept(item);
            }else{
                callback.accept(null);
            }
        });
        VisTable t = table(skin);
        t.add(selector);
        t.add(trig(skin, "-", ()->{
            if(selector.getItems().size > 0){
                items.removeValue(selector.getSelected(), false);
                map.remove(selector.getSelected());
                selector.setItems(items);
            }
        }));
        t.add(trig(skin, "+", ()->{
            String baseName = "new name";
            String name = baseName;
            for(int i=1 ;  ; i++){
                if(!map.containsKey(name)){
                    map.put(name, factory.get());
                    items.add(name);
                    break;
                }
                name = baseName + " " + i;
            }
            selector.setItems(items);
            selector.setSelected(name);
        }));
        t.add(trig(skin, "update", ()->{
            if(selector.getItems().size > 0){
                map.put(selector.getSelected(), factory.get());
            }
        }));
        t.add(trig(skin, "rename", ()->{
            if(selector.getItems().size > 0){
                Label typer = new Label("", skin);
                Stage stage = selector.getStage();
                Dialog dialog = dialog(typer, "Rename " + selector.getSelected(), skin).show(stage);
                stage.setKeyboardFocus(typer);
                InputListener listener = new InputListener(){
                    @Override
                    public boolean keyTyped(InputEvent event, char character) {
                        Label label = (Label)event.getListenerActor();
                        if(character == '\n'){
                            String key = selector.getSelected();
                            String newKey = label.getText().toString();
                            T object = map.get(key);
                            map.remove(key);
                            int index = items.indexOf(key, false);
                            items.set(index, newKey);
                            map.put(newKey, object);
                            selector.setItems(items);
                            selector.setSelected(newKey);
                            event.getStage().setKeyboardFocus(null);
                            dialog.hide();
                        }else{
                            label.setText(label.getText() + String.valueOf(character));
                        }
                        return true;
                    }
                };
                typer.addListener(listener);
            }
        }));
        return t;
    }

    public static void tooltip(Actor actor, String text) {
        TextTooltip tooltip = new TextTooltip(text, getSkin());
        tooltip.setInstant(true);
        actor.addListener(tooltip);

    }



}
