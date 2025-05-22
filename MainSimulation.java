import javax.swing.*;
import java.util.Locale;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class MainSimulation implements KeyListener {

    // Parametre robota a simulácie
    private double L = 0.2;
    private double deltaT_sim = 0.1;

    // Aktuálny status (lokalizacia zaciatocneho bodu)
    private double xT = 0, yT = 0, phi = 0;
    private double vT_current = 0, omegaT_current = 0;

    private volatile boolean continueSimulation = true;

    // Logovanie
    private List<String[]> logData = new ArrayList<>();

    public MainSimulation() {
        JFrame frame = new JFrame("Robot Control (Java) - Esc to Exit");
        frame.setSize(300, 100);
        frame.addKeyListener(this);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // pridavame zahlavie v loginge
        logData.add(new String[]{"Time_s", "XT_m", "YT_m", "phi_rad", "VT_mps", "omegaT_radps", "VL_mps", "VR_mps"});
    }

    //zaciatok simulacie
    public void runSimulation() {
        double currentTime = 0;
        while (continueSimulation) {
            // Výpočet kinematiky
            double vX = vT_current * Math.cos(phi);
            double vY = vT_current * Math.sin(phi);

            double xT_new = xT + vX * deltaT_sim;
            double yT_new = yT + vY * deltaT_sim;
            double phi_new = phi + omegaT_current * deltaT_sim;

            // V prípade potreby normalizácia phi_new (napr. na [-PI, PI])
            while (phi_new > Math.PI) phi_new -= 2 * Math.PI;
            while (phi_new < -Math.PI) phi_new += 2 * Math.PI;


            // Výpočet rýchlosti kolies
            double vR_calc = vT_current + (omegaT_current * L) / 2.0;
            double vL_calc = vT_current - (omegaT_current * L) / 2.0;

            // Logovanie (záznam stavu *pred* aktualizáciou pre aktuálny aktuálny čas)
            logData.add(new String[]{
                    String.format(Locale.US, "%.3f", currentTime),
                    String.format(Locale.US, "%.4f", xT),
                    String.format(Locale.US, "%.4f", yT),
                    String.format(Locale.US, "%.4f", phi),
                    String.format(Locale.US, "%.3f", vT_current),
                    String.format(Locale.US, "%.3f", omegaT_current),
                    String.format(Locale.US, "%.3f", vL_calc),
                    String.format(Locale.US, "%.3f", vR_calc)
            });

            // Aktualizácia stavu
            xT = xT_new;
            yT = yT_new;
            phi = phi_new;

            currentTime += deltaT_sim;

            try {
                Thread.sleep((long) (deltaT_sim * 1000));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                continueSimulation = false;
            }
        }
        saveLogToFile("robot_log_java.csv");
        System.exit(0); // terminate the application
    }

    private void saveLogToFile(String fileName) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(fileName))) {
            for (String[] record : logData) {
                pw.println(String.join(",", record));
            }
            System.out.println("Údaje sú uložené v " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        double vtIncrement = 0.1;
        double omegaIncrement = 0.1;
        int keyCode = e.getKeyCode();

        switch (keyCode) {
            case KeyEvent.VK_W: vT_current += vtIncrement; break;
            case KeyEvent.VK_S: vT_current -= vtIncrement; break;
            case KeyEvent.VK_A: omegaT_current += omegaIncrement; break; // Odbočenie doľava
            case KeyEvent.VK_D: omegaT_current -= omegaIncrement; break; // Odbočenie doprava
            case KeyEvent.VK_Q: vT_current = 0; break;
            case KeyEvent.VK_R: omegaT_current = 0; break;
            case KeyEvent.VK_SPACE: vT_current = 0; omegaT_current = 0; break;
            case KeyEvent.VK_ESCAPE: continueSimulation = false; break;
        }
        System.out.printf("Key: %s, VT: %.2f, omegaT: %.2f%n", KeyEvent.getKeyText(keyCode), vT_current, omegaT_current);
    }

    public static void main(String[] args) {
        MainSimulation sim = new MainSimulation();
        sim.runSimulation();
    }
}