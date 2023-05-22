package de.joshua.util.handler;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JMenuItemHandler extends JMenuItem {
    public JMenuItemHandler(String title, Runnable runnable){
        setText(title);
        addActionListener(e -> runnable.run());
    }


}
