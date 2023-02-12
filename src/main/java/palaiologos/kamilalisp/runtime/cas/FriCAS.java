package palaiologos.kamilalisp.runtime.cas;

import org.armedbear.lisp.Interpreter;
import palaiologos.kamilalisp.atom.Pair;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FriCAS {
    // Singleton API
    private static AtomicReference<FriCAS> instance = null;
    public static synchronized FriCAS getInstance() {
        if (instance == null) {
            instance = new AtomicReference<>(new FriCAS());
        }
        return instance.get();
    }

    // Normal API.
    public LinkedBlockingQueue<Integer> cin = new LinkedBlockingQueue<>();
    public LinkedBlockingQueue<String> cout = new LinkedBlockingQueue<>();
    public static InputStream nis = InputStream.nullInputStream();
    private Thread thread;

    private FriCAS() {
        URL resUrl = this.getClass().getResource("/lisp/fricas.lisp");
        String urlString = resUrl.toString();
        String str = urlString.substring(0, urlString.length() - "/fricas.lisp".length());
        thread = new Thread(() -> {
            List<Byte> cout = new ArrayList<>();
            Interpreter interpreter = Interpreter.createInstance(nis, new OutputStream() {
                @Override
                public void write(int b) {
                    cout.add((byte) b);
                    if(b == '\n') {
                        byte[] data = new byte[cout.size()];
                        for(int i = 0; i < cout.size(); i++) {
                            data[i] = cout.get(i);
                        }
                        FriCAS.this.cout.add(new String(data));
                        cout.clear();
                    }
                }
            }, str);
            interpreter.eval("(load \"fricas.lisp\")");
        });
        thread.setDaemon(true);
        thread.start();
        untilPrompt();
    }

    private Pair<String, String> untilPrompt() {
        List<String> result = new ArrayList<>();
        while(true) {
            try {
                String line = cout.take();
                if(line.matches("\\([0-9]+\\)\\ ->\\ \n")) {
                    break;
                }
                result.add(line);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return new Pair<>(String.join("\n", result.subList(0, result.size() - 1)).trim(), result.get(result.size() - 1).trim());
    }

    private EvaluationResult eval(String msg) {
        msg.codePoints().forEach(cin::add);
        Pair<String, String> s = untilPrompt();
        // XXX: Need to handle arithmetic display.
        boolean successful = s.snd().matches("^Type: .*$");
        String text, type;
        if(successful) {
            text = s.fst().replaceFirst("\\([0-9]+\\)", "").trim();
            type = s.snd().replaceFirst("Type: ", "");
        } else {
            text = s.fst() + "\n" + s.snd();
            type = "";
        }
        return new EvaluationResult(successful, text, type);
    }

    private static ReentrantLock lock = new ReentrantLock();

    public static synchronized Object withFriCas(Function<Function<String, EvaluationResult>, Object> callback) {
        lock.lock();
        FriCAS cas = FriCAS.getInstance();
        Object result;
        try {
            result = callback.apply(cas::eval);
        } catch(Exception e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
        return result;
    }

    public static void main(String[] args) {
        FriCAS cas = FriCAS.getInstance();
        // System.out.println(cas.eval("integrate(exp(1/x^2)/x^3,x)::InputForm\n").getResult());
        // System.out.println(cas.eval("limit(exp(-1/x^2),x=0)::InputForm\n").getResult());
        // System.out.println(cas.eval("limit(-x/x,x=%plusInfinity)::InputForm\n").getResult());
        Scanner s = new Scanner(System.in);
        System.out.println("FriCAS REPL.");
        while(s.hasNext()) {
            String m = s.nextLine();
            EvaluationResult r = cas.eval(m + "\n");
            if(r.isSuccessful())
                System.out.println(r.getResult() + "::" + r.getType());
            else
                System.out.println("Error: " + r.getResult());
        }
    }
}
