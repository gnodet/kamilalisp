package palaiologos.kamilalisp.runtime.graph;

import org.jgrapht.alg.lca.BinaryLiftingLCAFinder;
import org.jgrapht.alg.lca.TarjanLCAFinder;
import org.jgrapht.graph.DefaultEdge;
import palaiologos.kamilalisp.atom.Atom;
import palaiologos.kamilalisp.atom.Environment;
import palaiologos.kamilalisp.atom.Lambda;
import palaiologos.kamilalisp.atom.PrimitiveFunction;

import java.util.List;

public class TarjanLCA extends PrimitiveFunction implements Lambda {
    private static class GetLCA extends PrimitiveFunction implements Lambda {
        private final TarjanLCAFinder<Atom, DefaultEdge> finder;

        public GetLCA(TarjanLCAFinder<Atom, DefaultEdge> finder) {
            this.finder = finder;
        }

        @Override
        public Atom apply(Environment env, List<Atom> args) {
            assertArity(args, 2);
            return finder.getLCA(args.get(0), args.get(1));
        }

        @Override
        protected String name() {
            return "graph:tarjan-lca.get";
        }
    }

    @Override
    public Atom apply(Environment env, List<Atom> args) {
        assertArity(args, 2);
        GraphWrapper wp = args.get(0).getUserdata(GraphWrapper.class);
        Atom root = args.get(1);
        var lca = new TarjanLCAFinder<>(wp.getGraph(), root);
        return new Atom(new GetLCA(lca));
    }

    @Override
    protected String name() {
        return "graph:tarjan-lca";
    }
}
