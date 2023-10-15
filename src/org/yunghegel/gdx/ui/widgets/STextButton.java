package org.yunghegel.gdx.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import com.kotcrab.vis.ui.widget.Tooltip;
import org.yunghegel.gdx.ui.UI;

public class STextButton extends TextButton {

        public STextButton(String text) {
            super(text, UI.getSkin());
            Tooltip tooltip=new Tooltip();

        }

        public STextButton(String text, String styleName) {
            super(text, UI.getSkin(), styleName);
        }
}
