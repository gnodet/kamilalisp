package palaiologos.kamilalisp.runtime;

import palaiologos.kamilalisp.atom.Atom;
import palaiologos.kamilalisp.atom.Environment;
import palaiologos.kamilalisp.runtime.IO.*;
import palaiologos.kamilalisp.runtime.array.*;
import palaiologos.kamilalisp.runtime.array.carcdr.*;
import palaiologos.kamilalisp.runtime.array.hof.*;
import palaiologos.kamilalisp.runtime.array.sais.SacaBwt;
import palaiologos.kamilalisp.runtime.array.sais.SacaSais;
import palaiologos.kamilalisp.runtime.array.sais.SacaUnbwt;
import palaiologos.kamilalisp.runtime.dataformat.*;
import palaiologos.kamilalisp.runtime.datetime.*;
import palaiologos.kamilalisp.runtime.hashmap.*;
import palaiologos.kamilalisp.runtime.image.LoadImage;
import palaiologos.kamilalisp.runtime.image.WriteImage;
import palaiologos.kamilalisp.runtime.math.*;
import palaiologos.kamilalisp.runtime.math.bit.*;
import palaiologos.kamilalisp.runtime.math.cmp.*;
import palaiologos.kamilalisp.runtime.math.hyperbolic.*;
import palaiologos.kamilalisp.runtime.math.prime.IsPrime;
import palaiologos.kamilalisp.runtime.math.prime.NextPrime;
import palaiologos.kamilalisp.runtime.math.prime.PrimeFactors;
import palaiologos.kamilalisp.runtime.math.prime.PrimeNo;
import palaiologos.kamilalisp.runtime.math.trig.*;
import palaiologos.kamilalisp.runtime.matrix.LUDecomposition;
import palaiologos.kamilalisp.runtime.matrix.Trace;
import palaiologos.kamilalisp.runtime.matrix.Transpose;
import palaiologos.kamilalisp.runtime.meta.*;
import palaiologos.kamilalisp.runtime.net.*;
import palaiologos.kamilalisp.runtime.regex.RegexMatches;
import palaiologos.kamilalisp.runtime.regex.RegexReplace;
import palaiologos.kamilalisp.runtime.regex.RegexSplit;
import palaiologos.kamilalisp.runtime.string.*;

import java.math.BigDecimal;

public class FunctionRegistry {
    public static void registerDefault(Environment env) {
        env.setPrimitive("fr", new Atom(new BigDecimal(100)));
        env.setPrimitive("lambda", "λ", new Atom(new Dfn()));
        env.setPrimitive("def", "○←", new Atom(new GlobalBinding()));
        env.setPrimitive("car", "⍎", new Atom(Car.INSTANCE));
        env.setPrimitive("cdr", "⍕", new Atom(Cdr.INSTANCE));
        env.setPrimitive("caar", "⍎⍎", new Atom(new Caar()));
        env.setPrimitive("cadr", "⍎⍕", new Atom(new Cadr()));
        env.setPrimitive("cdar", "⍕⍎", new Atom(new Cdar()));
        env.setPrimitive("cddr", "⍕⍕", new Atom(new Cddr()));
        env.setPrimitive("reverse", "⌽", new Atom(new Reverse()));
        env.setPrimitive("rotate", "⊖", new Atom(new Rotate()));
        env.setPrimitive("range", "⍳", new Atom(new Range()));
        env.setPrimitive("foldl", "⌿←", new Atom(new Foldl()));
        env.setPrimitive("foldr", "⌿→", new Atom(new Foldr()));
        env.setPrimitive("foldl1", "⌿.←", new Atom(new Foldl1()));
        env.setPrimitive("foldr1", "⌿.→", new Atom(new Foldr1()));
        env.setPrimitive("lift", "⍏", new Atom(new Lift()));
        env.setPrimitive("tie", ",⍧", new Atom(new Tie()));
        env.setPrimitive("if", "↕", new Atom(new If()));
        env.setPrimitive("filter", "⍭", new Atom(new Filter()));
        env.setPrimitive("filter-idx", "⍭¨", new Atom(new FilterIdx()));
        env.setPrimitive("map-idx", "⍠¨", new Atom(new MapIdx()));
        env.setPrimitive("parallel-map-idx", "⍠∵", new Atom(new ParallelMapIdx()));
        env.setPrimitive("parallel-filter", "⍭∵", new Atom(new ParallelFilter()));
        env.setPrimitive("any", "∨?", new Atom(new Any()));
        env.setPrimitive("all", "∧?", new Atom(new All()));
        env.setPrimitive("none", "∅?", new Atom(new None()));
        env.setPrimitive("decode", "⊥⍟", new Atom(new Decode()));
        env.setPrimitive("encode", "⊤⍟", new Atom(new Encode()));
        env.setPrimitive("count", "⍴?", new Atom(new Count()));
        env.setPrimitive("sort-asc", "⊼", new Atom(new Sort()));
        env.setPrimitive("sort-desc", "⊻", new Atom(new SortDesc()));
        env.setPrimitive("scanl", "⍀←", new Atom(new Scanl()));
        env.setPrimitive("scanl1", "⍀.←", new Atom(new Scanl1()));
        env.setPrimitive("scanr", "⍀→", new Atom(new Scanr()));
        env.setPrimitive("scanr1", "⍀.→", new Atom(new Scanr1()));
        env.setPrimitive("replicate", "∥", new Atom(new Replicate()));
        env.setPrimitive("defun", "⍥←", new Atom(new Defun()));
        env.setPrimitive("outer-product", "⌼", new Atom(new OuterProduct()));
        env.setPrimitive("inner-product", "⌻", new Atom(new InnerProduct()));
        env.setPrimitive("meta:env-keys", new Atom(new EnvKeys()));
        env.setPrimitive("meta:to-glyphs", new Atom(new ToGlyphs()));
        env.setPrimitive("meta:to-ascii", new Atom(new ToASCII()));
        env.setPrimitive("levenshtein", "⍫≉", new Atom(new Levenshtein()));
        env.setPrimitive("shannon-entropy", "⍫⍴", new Atom(new Shannon()));
        env.setPrimitive("tally", "⍴", new Atom(new Tally()));
        env.setPrimitive("rank", "⍴⍴", new Atom(new Rank()));
        env.setPrimitive("same", "≡", new Atom(new Same()));
        env.setPrimitive("converge", "→≡", new Atom(new Converge()));
        env.setPrimitive("not-same", "≢", new Atom(new NotSame()));
        env.setPrimitive("same-elements", "⍉≡", new Atom(new SameElements()));
        env.setPrimitive("not-same-elements", "⍉≢", new Atom(new NotSameElements()));
        env.setPrimitive("grade-up", "⍋", new Atom(new GradeUp()));
        env.setPrimitive("grade-down", "⍒", new Atom(new GradeDown()));
        env.setPrimitive("cons", "⍟", new Atom(new Cons()));
        env.setPrimitive("pmat", "⍉↩⍳", new Atom(new Pmat()));
        env.setPrimitive("flatten", "∊", new Atom(new Flatten()));
        env.setPrimitive("let", "○⊢", new Atom(new Let()));
        env.setPrimitive("cond", "↕¨", new Atom(new Cond()));
        env.setPrimitive("cmpx", new Atom(new Cmpx()));
        env.setPrimitive("import", "○←⍫", new Atom(new Import()));
        env.setPrimitive("append", "⍠", new Atom(new Append()));
        env.setPrimitive("group", "⌸", new Atom(new Group()));
        env.setPrimitive("try-catch", "‼", new Atom(new TryCatch()));
        env.setPrimitive("raise", "↑‼", new Atom(new Raise()));
        env.setPrimitive("let-seq", "○⊢¨", new Atom(new LetSeq()));
        env.setPrimitive("while", "⍣", new Atom(new While()));
        env.setPrimitive("partial-while", "⍀⍣", new Atom(new PartialWhile()));
        env.setPrimitive("memo", "⌹↔", new Atom(new Memo()));
        env.setPrimitive("index-of", "∊?", new Atom(new IndexOf()));
        env.setPrimitive("to-digits", "⌹⊙", new Atom(new ToDigits()));
        env.setPrimitive("without", "⍪", new Atom(new Without()));
        env.setPrimitive("from-digits", "⊙⌹", new Atom(new FromDigits()));
        env.setPrimitive("cycle", "⍉↩", new Atom(new Cycle()));
        env.setPrimitive("take", "↑", new Atom(new Take()));
        env.setPrimitive("drop", "↓", new Atom(new Drop()));
        env.setPrimitive("unique-mask", "⊙¨", new Atom(new UniqueMask()));
        env.setPrimitive("unique", "⊙", new Atom(new Unique()));
        env.setPrimitive("intersection", "⋂", new Atom(new Intersection()));
        env.setPrimitive("union", "⋃", new Atom(new Union()));
        env.setPrimitive("parse-number", "⊙⍫", new Atom(new ParseNumber()));
        env.setPrimitive("prefixes", "ᑈ", new Atom(new Prefixes()));
        env.setPrimitive("suffixes", "ᐵ", new Atom(new Suffixes()));
        env.setPrimitive("interleave", "⍧", new Atom(new Interleave()));
        env.setPrimitive("take-while", "⍣↑", new Atom(new TakeWhile()));
        env.setPrimitive("drop-while", "⍣↓", new Atom(new DropWhile()));
        env.setPrimitive("find", "⍷", new Atom(new Find()));
        env.setPrimitive("where", "⍸", new Atom(new Where()));
        env.setPrimitive("powerset", "⍉⍉", new Atom(new Powerset()));
        env.setPrimitive("discard", "∅←", new Atom(new Discard()));
        env.setPrimitive("empty?", "⍠⍉?", new Atom(new IsNil()));
        env.setPrimitive("starts-with", "⍠⊂←", new Atom(new StartsWith()));
        env.setPrimitive("insert", "⍠⊣", new Atom(new Insert()));
        env.setPrimitive("insert-all", "⍠⊣∵", new Atom(new InsertAll()));
        env.setPrimitive("exit", "→⋄", new Atom(new Exit()));
        env.setPrimitive("shuffle", "⍠⍰", new Atom(new Shuffle()));
        env.setPrimitive("bipartition", "⍡¨", new Atom(new Bipartition()));
        env.setPrimitive("partition", "⍡", new Atom(new PartitionRange()));
        env.setPrimitive("windows", "⌹⇔⍡", new Atom(new Windows()));

        env.setPrimitive("img:write", "≣⊣", new Atom(new WriteImage()));
        env.setPrimitive("img:read", "≣⊢", new Atom(new LoadImage()));

        env.setPrimitive("prime:factors", "⋔⌹", new Atom(new PrimeFactors()));
        env.setPrimitive("prime:is?", "⋔?", new Atom(new IsPrime()));
        env.setPrimitive("prime:next", "⋔↑", new Atom(new NextPrime()));
        env.setPrimitive("prime:nth", "⋔→", new Atom(new PrimeNo()));

        env.setPrimitive("str:format", "⍫∊", new Atom(new Format()));
        env.setPrimitive("str:lines", new Atom(new Lines()));
        env.setPrimitive("str:implode", "⍫∨", new Atom(new Implode()));
        env.setPrimitive("str:lexer", "⍫∦", new Atom(new Lexer()));
        env.setPrimitive("str:implode-on", "⍫∨¨", new Atom(new ImplodeOn()));
        env.setPrimitive("str:explode", "⍫∧", new Atom(new Explode()));
        env.setPrimitive("to-string", "⍫←", new Atom(new ToString()));
        env.setPrimitive("str:escape", new Atom(new Escape()));
        env.setPrimitive("str:unescape", new Atom(new Unescape()));
        env.setPrimitive("str:contains", "⍫⊂←", new Atom(new Contains()));

        env.setPrimitive("io:writeln", "↑⍫", new Atom(new Writeln()));
        env.setPrimitive("io:readln", "↓⍫", new Atom(new Readln()));
        env.setPrimitive("io:write", "↗⍫", new Atom(new Write()));
        env.setPrimitive("io:get-file", "⍫⊢", new Atom(new GetFile()));
        env.setPrimitive("io:put-file", "⍫⊣", new Atom(new PutFile()));
        env.setPrimitive("io:get-file-buffer", "⍫⎕⊢", new Atom(new GetFileBuffer()));
        env.setPrimitive("io:put-file-buffer", "⍫⎕⊣", new Atom(new PutFileBuffer()));

        env.setPrimitive("saca:sais", new Atom(new SacaSais()));
        env.setPrimitive("saca:bwt", new Atom(new SacaBwt()));
        env.setPrimitive("saca:unbwt", new Atom(new SacaUnbwt()));

        env.setPrimitive("matrix:transpose", "⎕⍉", new Atom(new Transpose()));
        env.setPrimitive("matrix:LU", "⎕↙↗", new Atom(new LUDecomposition()));
        env.setPrimitive("matrix:trace", "⎕∑", new Atom(new Trace()));

        env.setPrimitive("bit:and", "⌶∧", new Atom(new BitAnd()));
        env.setPrimitive("bit:or", "⌶∨", new Atom(new BitOr()));
        env.setPrimitive("bit:xor", "⌶≠", new Atom(new BitXor()));
        env.setPrimitive("bit:nand", "⌶¬∧", new Atom(new BitNand()));
        env.setPrimitive("bit:not", "⌶¬", new Atom(new BitNot()));
        env.setPrimitive("bit:popcount", "⌶⍏", new Atom(new BitPopcount()));
        env.setPrimitive("bit:unpack", "⌶↓", new Atom(new BitUnpack()));
        env.setPrimitive("bit:pack", "⌶↑", new Atom(new BitPack()));

        env.setPrimitive("abs", new Atom(new Abs()));
        env.setPrimitive("bernoulli", new Atom(new Bernoulli()));
        env.setPrimitive("binomial", new Atom(new Binomial()));
        env.setPrimitive("**", new Atom(new DoubleStar()));
        env.setPrimitive("ln", new Atom(new Ln()));
        env.setPrimitive("sqrt", "√", new Atom(new Sqrt()));
        env.setPrimitive(">", new Atom(new Gt()));
        env.setPrimitive("<", new Atom(new Lt()));
        env.setPrimitive(">=", "≥", new Atom(new Ge()));
        env.setPrimitive("<=", "≤", new Atom(new Le()));
        env.setPrimitive("sin", new Atom(new Sin()));
        env.setPrimitive("cos", new Atom(new Cos()));
        env.setPrimitive("tan", new Atom(new Tan()));
        env.setPrimitive("asin", new Atom(new Asin()));
        env.setPrimitive("acos", new Atom(new Acos()));
        env.setPrimitive("atan", new Atom(new Atan()));
        env.setPrimitive("re", new Atom(new Re()));
        env.setPrimitive("complex-parts", new Atom(new ComplexParts()));
        env.setPrimitive("im", new Atom(new Im()));
        env.setPrimitive("csc", new Atom(new Csc()));
        env.setPrimitive("sec", new Atom(new Sec()));
        env.setPrimitive("cot", new Atom(new Cot()));
        env.setPrimitive("acsc", new Atom(new Acsc()));
        env.setPrimitive("asec", new Atom(new Asec()));
        env.setPrimitive("acot", new Atom(new Acot()));
        env.setPrimitive("sinh", new Atom(new Sinh()));
        env.setPrimitive("cosh", new Atom(new Cosh()));
        env.setPrimitive("tanh", new Atom(new Tanh()));
        env.setPrimitive("coth", new Atom(new Coth()));
        env.setPrimitive("sech", new Atom(new Sech()));
        env.setPrimitive("csch", new Atom(new Csch()));
        env.setPrimitive("asinh", new Atom(new Asinh()));
        env.setPrimitive("acosh", new Atom(new Acosh()));
        env.setPrimitive("atanh", new Atom(new Atanh()));
        env.setPrimitive("acoth", new Atom(new Acoth()));
        env.setPrimitive("asech", new Atom(new Asech()));
        env.setPrimitive("acsch", new Atom(new Acsch()));
        env.setPrimitive("log2", new Atom(new Log2()));
        env.setPrimitive("log10", new Atom(new Log10()));
        env.setPrimitive("gcd", new Atom(new Gcd()));
        env.setPrimitive("lcm", new Atom(new Lcm()));
        env.setPrimitive("gamma", "Γ", new Atom(new Gamma()));
        env.setPrimitive("not", "¬", new Atom(new Not()));
        env.setPrimitive("fib", new Atom(new Fib()));
        env.setPrimitive("ceil", "⌈", new Atom(new Ceil()));
        env.setPrimitive("floor", "⌊", new Atom(new Floor()));
        env.setPrimitive("round", new Atom(new Round()));
        env.setPrimitive("and", "∧", new Atom(new And()));
        env.setPrimitive("or", "∨", new Atom(new Or()));
        env.setPrimitive("exp", new Atom(new Exp()));
        env.setPrimitive("max", new Atom(new Max()));
        env.setPrimitive("min", new Atom(new Min()));
        env.setPrimitive("signum", new Atom(new Signum()));
        env.setPrimitive("true", "⊤", Atom.TRUE);
        env.setPrimitive("false", "⊥", Atom.FALSE);
        env.setPrimitive("=", new Atom(new Eq()));
        env.setPrimitive("/=", "≠", new Atom(new Neq()));
        env.setPrimitive("pi", "π", new Atom(new Pi()));
        env.setPrimitive("e", new Atom(new E()));
        env.setPrimitive("+", new Atom(new Plus()));
        env.setPrimitive("-", new Atom(new Minus()));
        env.setPrimitive("*", new Atom(new Star()));
        env.setPrimitive("/", new Atom(new Slash()));
        env.setPrimitive("mod", new Atom(new Mod()));
        env.setPrimitive("<=>", "⇔", new Atom(new Spaceship()));
        env.setPrimitive("ucs", new Atom(new Ucs()));
        env.setPrimitive("ucb", new Atom(new Ucb()));
        env.setPrimitive("match", "→", new Atom(new Match()));
        env.setPrimitive("random", "⍰", new Atom(new Random()));
        env.setPrimitive("fft", "⊙↖⍠", new Atom(new FFT()));
        env.setPrimitive("ifft", "⊙↘⍠", new Atom(new IFFT()));

        env.setPrimitive("regex:matches?", "⍫⊖∊?", new Atom(new RegexMatches()));
        env.setPrimitive("regex:replace", "⍫⊖⍆", new Atom(new RegexReplace()));
        env.setPrimitive("regex:split", "⍫⊖⍭", new Atom(new RegexSplit()));

        env.setPrimitive("xml:parse", new Atom(new XmlParse()));
        env.setPrimitive("xml:write", new Atom(new XmlWrite()));
        env.setPrimitive("xml:escape", new Atom(new XmlEscape()));
        env.setPrimitive("xml:unescape", new Atom(new XmlUnescape()));
        env.setPrimitive("csv:parse", new Atom(new CsvParse()));
        env.setPrimitive("csv:write", new Atom(new CsvWrite()));
        env.setPrimitive("json:parse", new Atom(new JsonParse()));
        env.setPrimitive("json:write", new Atom(new JsonWrite()));

        env.setPrimitive("codec:gzip-compress", new Atom(new GzipCompress()));
        env.setPrimitive("codec:gzip-decompress", new Atom(new GzipDecompress()));
        env.setPrimitive("codec:bzip2-compress", new Atom(new Bzip2Compress()));
        env.setPrimitive("codec:bzip2-decompress", new Atom(new Bzip2Decompress()));
        env.setPrimitive("codec:lz4-compress", new Atom(new Lz4Compress()));
        env.setPrimitive("codec:lz4-decompress", new Atom(new Lz4Decompress()));
        env.setPrimitive("codec:xz-compress", new Atom(new XzCompress()));
        env.setPrimitive("codec:xz-decompress", new Atom(new XzDecompress()));

        env.setPrimitive("date:from", new Atom(new DateTimeFrom()));
        env.setPrimitive("time:from", new Atom(new TimeFrom()));
        env.setPrimitive("date:add", new Atom(new DateTimeAdd()));
        env.setPrimitive("date:difference", new Atom(new DateTimeDifference()));
        env.setPrimitive("time:hours", new Atom(new TimeHours()));
        env.setPrimitive("time:minutes", new Atom(new TimeMinutes()));
        env.setPrimitive("time:seconds", new Atom(new TimeSeconds()));
        env.setPrimitive("time:nanoseconds", new Atom(new TimeNanoseconds()));
        env.setPrimitive("date:years", new Atom(new DateYears()));
        env.setPrimitive("date:months", new Atom(new DateMonths()));
        env.setPrimitive("date:days", new Atom(new DateDays()));
        env.setPrimitive("date:to-list", new Atom(new DateTimeToList()));
        env.setPrimitive("date:day-of-week", new Atom(new DateTimeDayOfWeek()));
        env.setPrimitive("date:now", new Atom(new DateNow()));
        env.setPrimitive("date:now-tz", new Atom(new DateNowTZ()));
        env.setPrimitive("time:now", new Atom(new TimeNow()));
        env.setPrimitive("time:now-tz", new Atom(new TimeNowTZ()));
        env.setPrimitive("date:parse", new Atom(new DateParse()));
        env.setPrimitive("time:parse", new Atom(new TimeParse()));

        env.setPrimitive("net:wget", new Atom(new Wget()));
        env.setPrimitive("net:fetch", new Atom(new Fetch()));
        env.setPrimitive("net:client", new Atom(new NetClient()));
        env.setPrimitive("net:client-ssl", new Atom(new NetClientSSL()));
        env.setPrimitive("net:server", new Atom(new NetServer()));
        env.setPrimitive("net:server-ssl", new Atom(new NetServerSSL()));

        env.setPrimitive("hashmap:from-list", "⍔⌿", new Atom(new HashMapFromList()));
        env.setPrimitive("hashmap:as-list", "⍔⍀", new Atom(new HashMapAsList()));
        env.setPrimitive("hashmap:size", "⍔⍴", new Atom(new HashMapSize()));
        env.setPrimitive("hashmap:key-list", "⍔⍎", new Atom(new HashMapKeyList()));
        env.setPrimitive("hashmap:value-list", "⍔⍕", new Atom(new HashMapValueList()));
        env.setPrimitive("hashmap:contains-key?", "⍔⍎?", new Atom(new HashMapContainsKey()));
        env.setPrimitive("hashmap:contains-value?", "⍔⍕?", new Atom(new HashMapContainsValue()));
        env.setPrimitive("hashmap:get", "⍔⍆", new Atom(new HashMapGet()));
        env.setPrimitive("hashmap:adjoin", "⍔+", new Atom(new HashMapAdjoin()));
        env.setPrimitive("hashmap:minus", "⍔-", new Atom(new HashMapMinus()));
        env.setPrimitive("hashmap:merge", "⍔⋃", new Atom(new HashMapMerge()));
        env.setPrimitive("hashmap:without", "⍔⍪", new Atom(new HashMapWithout()));
        env.setPrimitive("hashmap:group", "⍔⌸", new Atom(new HashMapGroup()));
    }
}
