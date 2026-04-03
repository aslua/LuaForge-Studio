package io.github.rosemoe.sora.widget.component;

public class TextActionButton {
    private final int id;
    private final int iconRes;
    private final int contentDescription;
    private boolean enabled;
    private boolean visible;

    public TextActionButton(int id, int iconRes, int contentDescription) {
        this.id = id;
        this.iconRes = iconRes;
        this.contentDescription = contentDescription;
        this.enabled = true;
        this.visible = true;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public int getIconRes() {
        return iconRes;
    }

    public int getContentDescription() {
        return contentDescription;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}