package vlakna.backend.threads;

import vlakna.Values;
import vlakna.backend.H;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 1. Implementace společného zámku na objektu ReentrantLock<br><br>
 * private final ReentrantLock lock = new ReentrantLock();<br><br>
 * 2. Kolekce typu Condition, která bude obsahovat odkazy (podmínky) pro přepínání jednotlivých vláken. V této kolekci
 * bude tolik objetků, kolik je vláken. Kolekce se tedy naplní tolika objekty typu Condition, kolik je vláken.
 * Nevytváříme nové objekty Collection, ale použijeme metodu z ReentratnLock zámku -> newCondition().
 * Tím je i Condition registrován k zámku.<br><br>
 * private final List<Condition> threadConditions = new ArrayList<>();<br>
 * ...<br>
 * for (int i = 0; i < threads.size(); i++) {<br>
 * threadConditions.add(lock.newCondition());<br>
 * }<br><br>
 * 3. Vytvoříme "řídící proměnnou" (takového Peška), kterou si budou vlákna přehazovat a tím budou říkat, kdo je na řadě.<br><br>
 * private int whoseTurn = 1;<br><br>
 * 4. Proces v každém vlákně nejdříve na začátku cyklu zamčeme.<br><br>
 * lock.lock();<br><br>
 * <i>Poznámka: Řídící proměnnou whoseTurn musíme používat synchronizovaně, protože ji budou využívat všechna vlákna.
 * V tomto koordinovaném přístupu se obejdeme nejen bez přímého synchronizovaného bloku, ale také bez metod wait() a notify().
 * Všechny tyto termíny jsou tady nahrazeny (obaleny) jinou syntaxí:
 * <ul>
 * <li>synchronized { ... } -> nahrazeno -> lock.lock(); ... lock.unlock();</li>
 * <li>notify() -> nahrazeno -> lock.signal()</li>
 * <li>wait() -> nahrazeno -> lock.await()</li>
 * </ul></i>
 * 5. Následuje try-catch blok, ve kterém budeme vlákno uspávat do té doby, dokud nebude na řadě
 * (jako kdybychom volali na vlákno metodu wait()). V sekci finally budeme zámek uvolňovat pomocí příkazu lock.unlock();<br><br>
 * try {<br>
 * while (whoseTurn != threadId) {<br>
 * threadConditions.get(threadId).await();<br>
 * }<br>
 * [CODE]<br>
 * [CONTROLL UNIT PROCESS]<br>
 * } catch (InterruptedException exp) {<br>
 * Thread.currentThread().interrupt();<br>
 * } finally {<br>
 * lock.unlock();<br>
 * }<br>
 * <br>
 * 6. Do předchozího kroku, ještě nahradíme tag [CONTROLL UNIT PROCESS] částí, která bude pracovat s řídící jednotkou
 * (prostě toto bude část, která bude něco zpracovávat, když je zrovna některé vlákno "na řadě")
 * Nejdříve řekneme, kdo bude další na řadě:<br><br>
 * whoseTurn = threadId + 1;<br>
 * if (whoseTurn == threadConditions.size()) {<br>
 * whoseTurn = 0;<br>
 * }<br><br>
 * No a potom toho, kdo bude na řadě, tak "probudíme" (je to to samé, jako kdybychom volali na to vlákno metodu notify())<br><br>
 * threadConditions.get(whoseTurn).signal();<br><br>
 * 7. kompletní kód v každém vlákně tedy bude vypadat následovně (s tím, že tag [CODE] nahradíme už vlastním kódem):<br><br>
 * lock.lock();<br>
 * try {<br>
 * while (whoseTurn != threadId) {<br>
 * threadConditions.get(threadId).await();<br>
 * }<br>
 * [CODE]<br>
 * whoseTurn = threadId + 1;<br>
 * if (whoseTurn == threadConditions.size()) {<br>
 * whoseTurn = 0;<br>
 * }<br>
 * threadConditions.get(whoseTurn).signal();<br>
 * } catch (InterruptedException exp) {<br>
 * Thread.currentThread().interrupt();<br>
 * } finally {<br>
 * lock.unlock();<br>
 * }<br>
 * ...<br><br>
 * <i>Poznámka na konec: Pokud bychom chtěli corektně ukončit vlákna, tak stačí v "synchronizačním bloku" všechna
 * vlákna probudit:<br>
 * lock.lock();<br>
 * threadConditions.getFirst().signalAll();<br>
 * lock.unlock();</i>
 */
public class GKoordinovana extends AbstractThread {

    private final List<AbstractThread> threads = new ArrayList<>();
    private final ReentrantLock lock = new ReentrantLock();
    private final List<Condition> threadConditions = new ArrayList<>();
    private String selected;
    private String choice;
    private int whoseTurn = 1;

    public void start(String choice) {
        this.choice = choice;
        super.start();
    }

    @Override
    public void run() {
        beManager.getFeManager().removeShape(this);
        if (choice.equals(selected)) {
            return;
        }
        stop();
        selected = choice;
        if (Values.THREAD_TYPE_COORDINATION.equals(choice)) {
            fillThreads(new int[]{0, 72, 144, 216, 278});
        } else {
            fillThreads(new int[]{0, Values.SHAPE_RADIUS, 2 * Values.SHAPE_RADIUS, 3 * Values.SHAPE_RADIUS, 4 * Values.SHAPE_RADIUS}, Style.FALL_DOWN, askDialog());
        }
    }

    @Override
    public void stop() {
        super.stop();
        if (!threadConditions.isEmpty()) {
            lock.lock();
            threadConditions.getFirst().signalAll();
            lock.unlock();
        }
        H.sleep(100);
        threads.forEach(AbstractThread::stop);
        threads.clear();
    }

    private void fillThreads(int[] positions) {
        fillThreads(positions, Style.MOVE_AROUND, 1);
    }

    private void fillThreads(int[] positions, Style style, int outputDialog) {
        if (outputDialog == 2) {
            selected = null;
            return;
        }
        int threadCount = 0;
        StringBuilder tab = new StringBuilder();
        for (int position : positions) {
            int threadId = threadCount++;
            String tabulator = tab.toString();
            threads.add(new AbstractThread() {
                @Override
                public void run() {
                    if (style == Style.MOVE_AROUND) {
                        moveAround(this, position, threadId);
                    } else {
                        fallDown(this, position, outputDialog, threadId, tabulator);
                    }
                    selected = null;
                }
            });
            tab.append("\t");
        }
        threadConditions.clear();
        for (int i = 0; i < threads.size(); i++) {
            threadConditions.add(lock.newCondition());
        }
        stopped = false;
        threads.forEach(AbstractThread::start);
    }

    private void moveAround(AbstractThread thread, int position, int threadId) {
        int i = position;
        while (!stopped) {
            lock.lock();
            try {
                while (whoseTurn != threadId) {
                    threadConditions.get(threadId).await();
                }

                if (!pause) {
                    beManager.getFeManager().moveShape(thread, getNewPosition(i));
                    i++;
                }

                whoseTurn = threadId + 1;
                if (whoseTurn == threadConditions.size()) {
                    whoseTurn = 0;
                }
                threadConditions.get(whoseTurn).signal();
            } catch (InterruptedException exp) {
                Thread.currentThread().interrupt();
            } finally {
                lock.unlock();
            }
            H.sleep(1);
        }
    }

    private void fallDown(AbstractThread thread, int startingPosition, int output, int threadId, String tab) {
        int i = 1;
        int move = 2;
        while (!stopped) {
            lock.lock();
            try {
                while (whoseTurn != threadId) {
                    threadConditions.get(threadId).await();
                }

                if (!pause) {
                    beManager.getFeManager().moveShape(thread, startingPosition, i);
                    output(output, tab, thread);
                    i += move;
                    if (i >= beManager.getFeManager().getHeight() - Values.SHAPE_RADIUS || i <= 0) {
                        move = -move;
                    }
                }

                whoseTurn = threadId + 1;
                if (whoseTurn == threadConditions.size()) {
                    whoseTurn = 0;
                }
                threadConditions.get(whoseTurn).signal();
            } catch (InterruptedException exp) {
                Thread.currentThread().interrupt();
            } finally {
                lock.unlock();
            }
            H.sleep(1);
        }
    }

    private void output(int output, String tab, AbstractThread thread) {
        if (output == 0) {
            beManager.getFeManager().printText(tab + thread + "\n");
        }
    }
}
