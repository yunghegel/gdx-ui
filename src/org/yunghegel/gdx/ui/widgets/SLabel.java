package org.yunghegel.gdx.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import org.yunghegel.gdx.ui.UI;

public class SLabel extends Label {
    public SLabel(String text) {
        super(text, UI.getSkin());
    }
    public SLabel(String text, String styleName) {
        super(text, UI.getSkin(), styleName);
    }
}
