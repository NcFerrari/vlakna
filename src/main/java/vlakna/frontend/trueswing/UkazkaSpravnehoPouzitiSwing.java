package vlakna.frontend.trueswing;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;
import java.awt.Color;
import java.awt.Graphics;
import java.util.List;
import java.util.concurrent.Future;

import vlakna.Values;
import vlakna.backend.H;

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
 */
public class UkazkaSpravnehoPouzitiSwing {

    private JLabel label;
    private JPanel panel;
    private Color color = Color.GREEN;
    private BackgroundTask backgroundTask = new BackgroundTask();

    public UkazkaSpravnehoPouzitiSwing() {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setSize(400, 300);
            frame.setLocationRelativeTo(null);
            frame.setLayout(null);
            frame.setVisible(true);

            panel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.setColor(color);
                    g.fillOval(0, 0, getWidth(), getHeight());
                }
            };
            panel.setSize(50, 50);
            frame.add(panel);
            panel.repaint();
            panel.revalidate();

            JButton button = new JButton(Values.START_PROCESS);
            frame.add(button);
            button.setSize(175, 25);
            button.setLocation(50, 0);
            button.setBackground(Color.yellow);
            button.addActionListener(e -> {
                if (!backgroundTask.isDone() || !backgroundTask.isCancelled()) {
                    backgroundTask.execute();
                }
            });

            label = new JLabel();
            label.setSize(button.getSize());
            label.setLocation(50, 25);
            frame.add(label);
        });
    }

    /**
     * <br>
     * Logika je taková, že:
     * <ol>
     *     <li>Vytvoří se nový objekt typu SwingWorker</li>
     *     <li>První datový typ v genercie určuje, jakou návratovou hodnotu bude mít metoda doInBackground<br>
     *         Druhý datový typ v generice určuje, s jakou hodnotou se bude pracovat mezi metodami publish a process</li>
     *         <li>Spuštění vlákna tohoto objektu provádí metoda execute()</li>
     *         <li>Tělo vlákna zapisujeme do metody doInBackground() (přepíšeme ji v potomkovi typu SwingWorker)</li>
     *         <li>Pokud chceme v průběhu vlákna upravovat i GUI, tak použijeme metodu publsih (parametr této metody je
     *         neomezené pole takového datového typu, jaký nastavíme v druhém parametru generiky)</li>
     *         <li>Tato metoda "předá" průběžný výsledek do nitra třdy</li>
     *         <li>Abychom s tímto výsledkem pracovali (abychom jej zobrazili na GUI), tak překryjeme metodu process,
     *         která dostává jako parametr List datových typů, které určíme v druhém parametru generiky)</li>
     *         <li>V metodě process si už zobrazíme průběžný výsledek, kde chceme</li>
     *         <li>Jakmile vlákno dokončí svou činnost, zavolá se metoda done(). Tu si můžeme překrýt dle libosti</li>
     * </ol>
     */
    private class BackgroundTask extends SwingWorker<Void, String> {

        @Override
        protected Void doInBackground() {
            for (int i = 5; i > 0; i--) {
                publish(String.format(Values.READY_IN, i));
                H.sleep(1000);
            }
            return null;
        }

        @Override
        protected void process(List<String> chunks) {
            color = Color.RED;
            panel.repaint();
            label.setText(chunks.getFirst());
        }

        @Override
        protected void done() {
            label.setText(Values.COMPLETED);
            color = Color.GREEN;
            panel.repaint();
            backgroundTask = new BackgroundTask();
        }
    }

}
