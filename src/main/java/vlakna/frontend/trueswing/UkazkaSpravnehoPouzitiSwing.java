package vlakna.frontend.trueswing;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;
import java.awt.Color;
import java.awt.Graphics;

/**
 * Všechny operace s GUI by měly běžet v EDT (Event Dispatch Thread)<br>
 * Všechno by tedy mělo běžet v SwingUtilities.invokeLater() lambdě, aby vše běželo správně ve vlákně<br><br>
 * V případě, že chceme upravovat komponentu, aniž by se měnil její rozměr nebo pozice, tak po každém vykreslení je třeba
 * volat metodu repaint na danou komponentu.
 * V případě, že měníme pozici nebo rozměr, tak kromě metody repaint je potřeba zavolat i metodu revalidate.<br><br>
 * Pokud chceme vykreslovat tvary, tak to vytváříme v samostatné třídě, které dědí z JComponent. JComponent obsahuje
 * 3 podobné metody:
 * <ul>
 *     <li>paint(Graphics)</li>
 *     <li>paintComponent(Graphics)</li>
 *     <li>paintComponents(Graphics)</li>
 * </ul>
 * Rozdíl mezi nimi je pro nás v použití.<br>
 * paint(Graphics) metoda: to je základ, kde se kompletně vykresluje samotný tvar komponenty. Do této metody bychom neměli vůbec
 * zasahovat.<br>
 * paintComponent(Graphics) metoda: toto je metoda, kterou můžeme přerývat. Tato metoda "přidá na" vytvořenou komponentu naše výtvory
 * Správné použití je tedy přepisování této metody.<br>
 * paintComponents(Graphics) metoda: tato metod je potřeba, pokud chceme, aby se po našem hraní v metodě paintComponent
 * znovu překreslily všechny prvky na komponentě. Pokud ji tedy chceme použít, tak na konci překrývání metody paintComponent
 * zavoláme super.paintComponents(g); Není to ovšem nutné. Tuto metodu také nepřekrýváme!<br><br>
 * Není potřeba přímo vytvářet JComponent (i když je to čistší a přehlednější). Můžeme kupříkladu překrýt metodu paintComponent
 * u třídy JPanel (viz ukázka).<br><br>
 * Pokud chceme pracovat s vláknem na pozadí, tak můžeme vytvářet vlastní vlákna (není to špatně), ale lepší způsob je použít
 * třídu SwingWorker. Ta nám usnadňuje správu vláken a bezpečnější použití. Navíc má tyto vlastnosti:<br>
 * <ul>
 *      <li>Automaticky spustí úlohu na pozadí mimo EDT.</li>
 *      <li>Má metody pro aktualizaci GUI ve správném vlákně (done(), process(), publish()).</li>
 *      <li>Podporuje návratovou hodnotu, takže může vrátit výsledek.</li>
 *      <li>Správně se integruje s Progress Bar (JProgressBar) a umožňuje průběžnou aktualizaci UI.</li>
 *      <li>Java ho spravuje (není třeba se starat o ukončení vlákna).</li>
 * </ul>
 *
 * <table>
 *     <tr>
 *         <th>Metoda</th>
 *         <th>Kdy se spustí?</th>
 *         <th>Běží v EDT?</th>
 *         <th>Popis</th>
 *     </tr>
 *     <tr>
 *         <td>doInBackground()</td>
 *         <td>Po spuštění execute()</td>
 *         <td>NE</td>
 *         <td>Tady běží hlavní úloha na pozadí</td>
 *     </tr>
 *     <tr>
 *         <td>done()</td>
 *         <td>Po dokončení doInBackground()</td>
 *         <td>ANO</td>
 *         <td>Zde můžeš aktualizovat GUI s výsledkem</td>
 *     </tr>
 *     <tr>
 *         <td>publish(V... chunks)</td>
 *         <td>Během doInBackground()</td>
 *         <td>NE</td>
 *         <td>Průběžně posílá data do process()</td>
 *     </tr>
 *     <tr>
 *         <td>process(List<V>chunks)</td>
 *         <td>Po zavolání publish()</td>
 *         <td>ANO</td>
 *         <td>Slouží k průběžné aktualizaci UI</td>
 *     </tr>
 *     <tr>
 *         <td>get()</td>
 *         <td>Po dokončení</td>
 *         <td>ANO</td>
 *         <td>Vrací výsledek doInBackground()</td>
 *     </tr>
 * </table>
 *
 */
public class UkazkaSpravnehoPouzitiSwing {

    public UkazkaSpravnehoPouzitiSwing() {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setSize(400, 300);
            frame.setLocationRelativeTo(null);
            frame.setLayout(null);
            frame.setVisible(true);

            JPanel panel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.setColor(Color.magenta);
                    g.fillOval(0, 0, getWidth(), getHeight());
                }
            };
            panel.setSize(50, 50);
            frame.add(panel);
            panel.repaint();
            panel.revalidate();

            JButton button = new JButton("Spustit úlohu");
            frame.add(button);
            button.setSize(125, 25);
            button.setLocation(50, 0);
            button.setBackground(Color.yellow);
            button.addActionListener(e -> new BackgroundTask().execute());
            button.repaint();
            button.revalidate();
        });
    }

    public static void main(String[] args) {
        new UkazkaSpravnehoPouzitiSwing();
    }

    private class BackgroundTask extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() throws Exception {
            Thread.sleep(3000);
            return null;
        }

        @Override
        protected void done() {
            System.out.println("completed!");
        }
    }

}
