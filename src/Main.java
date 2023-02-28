import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    public static BlockingQueue A_AMOUNT = new ArrayBlockingQueue<>(100);
    public static BlockingQueue B_AMOUNT = new ArrayBlockingQueue<>(100);
    public static BlockingQueue C_AMOUNT = new ArrayBlockingQueue<>(100);

    static AtomicInteger A = new AtomicInteger();
    static AtomicInteger B = new AtomicInteger();
    static AtomicInteger C = new AtomicInteger();

    public static void main(String[] args) throws InterruptedException {

        Random random = new Random();
        String[] texts = new String[10_000];
        for (int i = 0; i < texts.length; i++) {
            texts[i] = generateText("abc", 100_000);
        }

        Runnable generateWords = () -> {

                try {
                    for (int i = 0; i < 10_000; i++) {
                        A_AMOUNT.put(texts[i]);
                        B_AMOUNT.put(texts[i]);
                        C_AMOUNT.put(texts[i]);
                    }
                } catch (InterruptedException e) {
                    return;
                }
        };

        Thread cloneThread = new Thread(generateWords);
        cloneThread.start();

        Runnable aExamination = () -> {
            for (int i = 0; i < 10_000; i++) {
                String word = String.valueOf(A_AMOUNT.element());
                int letterCounter = (int) word.chars().filter(ch -> ch == 'a').count();
                if (letterCounter > A.get()) {
                    A.getAndSet(letterCounter);
                } else {
                    try {
                        A_AMOUNT.take();
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            }
        };
        Thread aThread = new Thread(aExamination);
        aThread.start();

        Runnable bExamination = () -> {
            for (int i = 0; i < 10_000; i++) {
                String word = String.valueOf(B_AMOUNT.element());
                int letterCounter = (int) word.chars().filter(ch -> ch == 'b').count();
                if (letterCounter > B.get()) {
                    B.getAndSet(letterCounter);
                    B_AMOUNT.clear();
                    try {
                        B_AMOUNT.put(word);
                    } catch (InterruptedException e) {
                        return;
                    }
                } else {
                    try {
                        B_AMOUNT.take();
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            }
        };

        Thread bThread = new Thread(bExamination);
        bThread.start();

        Runnable cExamination = () -> {
            for (int i = 0; i < 10_000; i++) {
                String word = String.valueOf(C_AMOUNT.element());
                int letterCounter = (int) word.chars().filter(ch -> ch == 'c').count();
                if (letterCounter > C.get()) {
                    C.getAndSet(letterCounter);
                    C_AMOUNT.clear();
                    try {
                        C_AMOUNT.put(word);
                    } catch (InterruptedException e) {
                        return;
                    }
                } else {
                    try {
                        C_AMOUNT.take();
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            }
        };

        Thread cTread = new Thread(cExamination);
        cTread.start();

        aThread.join();
        bThread.join();
        cTread.join();

        System.out.println("Самое длинное слово с буквой \"а\" " + A + "\n" +
                "Самое длинное слово с буквой \"b\" " + B + "\n" +
                "Самое длинное слово с буквой \"c\" " + C + "\n");
        cloneThread.interrupt();

    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}
