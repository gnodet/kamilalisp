package palaiologos.kamilalisp.runtime.graph;

import org.jgrapht.traverse.DepthFirstIterator;
import org.jgrapht.traverse.RandomWalkVertexIterator;
import palaiologos.kamilalisp.atom.*;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class RS extends PrimitiveFunction implements Lambda {
    @Override
    public Atom apply(Environment env, List<Atom> args) {
        GraphWrapper w = args.get(0).getUserdata(GraphWrapper.class);
        Atom start = args.get(1);
        Callable op = args.get(2).getCallable();
        Atom init = args.size() > 3 ? args.get(3) : Atom.NULL;
        Iterator<Atom> it = new RandomWalkVertexIterator<>(w.getGraph(), start);
        while (it.hasNext()) {
            Atom a = it.next();
            List<Atom> result = op.apply(env, List.of(a, init)).getList();
            init = result.get(0);
            if (result.get(1).coerceBool())
                break;
        }
        return init;
    }

    @Override
    protected String name() {
        return "graph:rs";
    }
}
