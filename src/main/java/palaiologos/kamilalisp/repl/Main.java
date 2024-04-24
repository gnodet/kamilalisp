package palaiologos.kamilalisp.repl;

import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.reader.impl.DefaultParser;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.AttributedStringBuilder;
import palaiologos.kamilalisp.atom.*;
import palaiologos.kamilalisp.error.InterruptionError;
import palaiologos.kamilalisp.runtime.ide.DarkTheme;
import palaiologos.kamilalisp.runtime.ide.IDE;
import palaiologos.kamilalisp.runtime.meta.Import;
import palaiologos.kamilalisp.runtime.remote.Server;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

public class Main {
    public static Environment defaultRegistry;

    public static boolean isBuiltin(String name) {
        return defaultRegistry.keys().contains(name);
    }

    private static void banner() {
        System.out.println("KamilaLisp v " + Version.version + " - Copyright (C) Kamila Szewczyk, 2021 - 2023.");
        System.out.println("Interactive mode.");
        System.out.println();
    }

    private static void evalScript(Environment env, String source, String[] args) throws IOException {
        List<Atom> data = Parser.parse(Files.readString(Path.of(source)));
        env.setPrimitive("args", new Atom(Arrays.stream(args).skip(1).map(Atom::new).toList()));
        try {
            for (Atom atom : data) {
                Evaluation.safeEvaluate(env, atom, (x, t) -> {
                    System.out.println(x);
                    throw new InterruptionError();
                });
            }
        } catch (InterruptionError e) {
            // Ignore, we just wanted to unwind the stack.
        }
    }

    private static void terminalTTY(String preload) throws IOException {
        banner();
        Environment env = new Environment(defaultRegistry);
        if (preload != null) {
            env.get("import").getCallable().apply(env, List.of(new Atom(preload)));
        }
        DefaultParser parser = new JLineParser();
        Terminal t = TerminalBuilder.builder().dumb(true).build();
        // Bug workaround for JLine3.
        String promptPattern = t.getType().equals(Terminal.TYPE_DUMB) || t.getType().equals(Terminal.TYPE_DUMB_COLOR) ? "" : "%P. ";
        LineReader r = LineReaderBuilder
                .builder()
                .appName("KamilaLisp")
                .parser(parser)
                .history(new DefaultHistory())
                .highlighter(new LispHighlight(env))
                .variable(LineReader.SECONDARY_PROMPT_PATTERN, promptPattern)
                .variable(LineReader.INDENTATION, 3)
                .terminal(t)
                .completer(new LispCompletion(env))
                .build();
        int lineNumberOffset = 0;
        try {
            while (true) {
                if (env.has("prompt")) {
                    Callable prompt = env.get("prompt").getCallable();
                    Atom a = Evaluation.safeEvaluate(env, prompt, List.of(), (s) -> {
                        System.err.println(s);
                        env.remove("prompt");
                        throw new InterruptionError();
                    });
                    if (a.getType() == Type.STRING && !a.getString().isEmpty()) {
                        AttributedStringBuilder b = AttributedStringParser.parse(a.getString());
                        r.printAbove(b.toAttributedString());
                    }
                }
                String code = r.readLine("--> ");
                if (code.isEmpty() || code.trim().isEmpty() || code.trim().charAt(0) == ';')
                    continue;
                try {
                    List<Atom> data;
                    try {
                        String cc;
                        if (code.charAt(0) == '?') {
                            cc = code.substring(1);
                        } else if (code.charAt(0) == '(' && code.charAt(code.length() - 1) == ')') {
                            cc = code;
                        } else {
                            cc = "(" + code + "\n)";
                        }
                        data = Parser.parse(lineNumberOffset, cc);
                    } catch (RuntimeException e) {
                        System.out.println("Syntax error: " + e.getMessage());
                        continue;
                    }
                    for (Atom atom : data) {
                        Atom a = Evaluation.safeEvaluate(env, atom, (s, thr) -> {
                            System.err.println(s);
                            throw new InterruptionError();
                        });
                        try {
                            if (a.equals(Atom.NULL))
                                continue;
                            System.out.println(a.toDisplayString());
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }
                } catch (InterruptionError e) {
                    // Ignore, we just wanted to unwind the stack.
                }
                // count newlines in code
                lineNumberOffset += code.chars().filter(c -> c == '\n').count();
            }
        } catch (EndOfFileException e) {
            System.out.println("Bye.");
        } catch (UserInterruptException e) {
            System.out.println("Interrupted.");
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            defaultRegistry = Environment.defaultEnvironment(false);
            terminalTTY(null);
        }
        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("--ide")) {
                defaultRegistry = Environment.defaultEnvironment(false);
                DarkTheme.installLafInfo();
                DarkTheme.setup();
                Enumeration<Object> keys = UIManager.getDefaults().keys();
                var fr = new javax.swing.plaf.FontUIResource(IDE.apl333Font);
                while (keys.hasMoreElements()) {
                    Object key = keys.nextElement();
                    Object value = UIManager.get(key);
                    if (value instanceof javax.swing.plaf.FontUIResource)
                        UIManager.put(key, fr);
                }
                IDE ide = new IDE();
            } else if (args[0].equalsIgnoreCase("--preload")) {
                defaultRegistry = Environment.defaultEnvironment(false);
                terminalTTY(args[1]);
            } else if (args[0].equalsIgnoreCase("--preload-sandboxed")) {
                defaultRegistry = Environment.defaultEnvironment(true);
                terminalTTY(args[1]);
            } else if (args[0].equalsIgnoreCase("--remote")) {
                defaultRegistry = Environment.defaultEnvironment(false);
                int port = 0;
                if (args.length >= 2)
                    port = Integer.parseInt(args[1]);
                Server.run(port);
            } else if (args[0].equalsIgnoreCase("--remote-sandboxed")) {
                defaultRegistry = Environment.defaultEnvironment(true);
                int port = 0;
                if (args.length >= 2)
                    port = Integer.parseInt(args[1]);
                Server.run(port);
            } else {
                if(args[0].equalsIgnoreCase("--sandboxed"))
                    defaultRegistry = Environment.defaultEnvironment(true);
                else
                    defaultRegistry = Environment.defaultEnvironment(false);
                evalScript(new Environment(defaultRegistry), args[0], args);
            }
        }
    }
}
