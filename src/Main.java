import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {

    public static BlockingQueue<String> A_AMOUNT = new ArrayBlockingQueue<>(100);
    public static BlockingQueue<String> B_AMOUNT = new ArrayBlockingQueue<>(100);
    public static BlockingQueue<String> C_AMOUNT = new ArrayBlockingQueue<>(100);

    public static void main(String[] args) throws InterruptedException {

        Thread cloneThread = new Thread(() -> {
            for (int i = 0; i < 10_000; i++) {
                String word = generateText("abc", 100_000);
                try {
                    A_AMOUNT.put(word);
                    B_AMOUNT.put(word);
                    C_AMOUNT.put(word);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        cloneThread.start();

        Runnable aExamination = () -> {
            char letter = 'a';
            int maxA = maxChar(A_AMOUNT, letter);
            System.out.println("Самое большое количество повторений \"а\" " + maxA);
        };
        Thread aThread = new Thread(aExamination);
        aThread.start();

        Runnable bExamination = () -> {
            char letter = 'b';
            int maxA = maxChar(B_AMOUNT, letter);
            System.out.println("Самое большое количество повторений \"b\" " + maxA);
        };

        Thread bThread = new Thread(bExamination);
        bThread.start();

        Runnable cExamination = () -> {
            char letter = 'c';
            int maxA = maxChar(C_AMOUNT, letter);
            System.out.println("Самое большое количество повторений \"c\" " + maxA);
        };

        Thread cTread = new Thread(cExamination);
        cTread.start();

        aThread.join();
        bThread.join();
        cTread.join();

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

    public static int maxChar(BlockingQueue<String> blockingQueue, char ch) {
        int count = 0;
        int max = 0;
        String word;
        for (int i = 0; i < 10_000; i++) {
            try {
                word = blockingQueue.take();
                for (char c : word.toCharArray()) {
                    if (c == ch) count++;
                }
                if (count > max) max = count;
                count = 0;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return max;
    }

}
