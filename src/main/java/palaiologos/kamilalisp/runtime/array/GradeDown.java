package palaiologos.kamilalisp.runtime.array;

import palaiologos.kamilalisp.atom.*;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.IntStream;

public class GradeDown extends PrimitiveFunction implements Lambda {
    @Override
    public Atom apply(Environment env, List<Atom> args) {
        if (args.size() == 1) {
            return new Atom(IntStream.range(0, args.get(0).getList().size()).boxed().sorted((o1, o2) -> -args.get(0).getList().get(o1).compareTo(args.get(0).getList().get(o2))).map(x -> new Atom(BigInteger.valueOf(x))).toList());
        } else if (args.size() == 2) {
            Callable reductor = args.get(1).getCallable();
            return new Atom(IntStream.range(0, args.get(0).getList().size()).boxed().sorted((o1, o2) -> -Evaluation.evaluate(env, reductor,
                            List.of(args.get(0).getList().get(o1),
                                    args.get(0).getList().get(o2)))
                    .getInteger().intValueExact()).map(x -> new Atom(BigInteger.valueOf(x))).toList());
        } else {
            throw new RuntimeException("Wrong number of arguments to grade-down: expected 1 or 2, got " + args.size());
        }
    }

    @Override
    protected String name() {
        return "grade-down";
    }
}
