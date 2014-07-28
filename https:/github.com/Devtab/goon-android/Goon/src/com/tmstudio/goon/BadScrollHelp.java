package com.tmstudio.goon;

public class BadScrollHelp {
    private static int scrollX = 0;
    private static int currentScreen = 0;

    public static synchronized void setScrollX(int scroll){
        scrollX = scroll;
    }

    public static synchronized void setCurrentScreen(int screen){
        currentScreen = screen;
    }

    public static synchronized int getScrollX(){
        return scrollX;
    }

    public static synchronized int getCurrentScreen(){
        return currentScreen;
    }

}
