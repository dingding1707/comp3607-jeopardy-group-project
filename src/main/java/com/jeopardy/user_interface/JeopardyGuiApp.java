package com.jeopardy.user_interface;

import javax.swing.SwingUtilities;

/**
 * Entry point for the Swing GUI version of the Jeopardy game.
 */
public class JeopardyGuiApp {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainWindow window = new MainWindow();
            window.setVisible(true);
        });
    }
}
