import com.afogleson.m3u.parser.M3UFileParser;
import com.afogleson.m3u.parser.M3U_Entry;
import com.afogleson.ui.DisplayKey;
import com.afogleson.ui.HandlerPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class M3uHandler {

    public static void main(String[] args) {
        Calendar now = Calendar.getInstance();
        int iStartYear = now.get(Calendar.YEAR);
        JFrame f = new JFrame(DisplayKey.get("gui.window.title"));
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = 640;
        int height = 240;
        f.setMinimumSize(new Dimension(width, height));
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
            }
        });
        HandlerPanel panel = new HandlerPanel(f);
        panel.setYearValues(iStartYear);
        f.setContentPane((JPanel)panel);
        f.pack();
        f.setVisible(true);
    }
}
