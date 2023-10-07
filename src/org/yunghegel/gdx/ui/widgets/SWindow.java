package org.yunghegel.gdx.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import org.yunghegel.gdx.ui.UI;
import org.yunghegel.gdx.ui.widgets.viewport.SImageButton;

public class SWindow extends Window {
    public SWindow(String title) {
        super(title, UI.getSkin());
    }

    public void addCloseButton(){
        Label titleLabel = getTitleLabel();
        Table titleTable = getTitleTable();
        SImageButton closeButton = new SImageButton("close-window");
        titleTable.add(closeButton).padRight(-getPadRight() + 1f).size(25,26).padRight(5);
        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                remove();
            }
        });
        closeButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                event.cancel();
                return true;
            }
        });

        if (titleLabel.getLabelAlign() == Align.center && titleTable.getChildren().size == 2)
            titleTable.getCell(titleLabel).padLeft(closeButton.getWidth() * 2);
    }
}
