package codex.codex_iter.www.awol.theme;


public class ThemeItem {
    public ThemeItem(int mainColor, int theme, boolean isDark) {
        this.mainColor = mainColor;
        this.theme = theme;
        this.isDark = isDark;
    }

    private int drawableId;
    private int mainColor;
    private int theme;
    private boolean isDark;

    public int getTheme() {
        return theme;
    }

    public void setTheme(int theme) {
        this.theme = theme;
    }

    public int getMainColor() {
        return mainColor;
    }

    public void setMainColor(int mainColor) {
        this.mainColor = mainColor;
    }

    public int getDrawableId() {
        return drawableId;
    }

    public void setDrawableId(int drawableId) {
        this.drawableId = drawableId;
    }

    public boolean isDark() {
        return isDark;
    }

    public void setDark(boolean dark) {
        isDark = dark;
    }
}
