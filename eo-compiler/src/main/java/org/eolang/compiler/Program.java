/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 eolang.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.eolang.compiler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.cactoos.Func;
import org.cactoos.Input;
import org.cactoos.Output;
import org.cactoos.func.And;
import org.cactoos.func.IoCheckedScalar;
import org.cactoos.io.FileAsOutput;
import org.cactoos.io.LengthOfInput;
import org.cactoos.io.TeeInput;
import org.eolang.compiler.syntax.Tree;

/**
 * Program.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.1
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class Program {

    /**
     * Text to parse.
     */
    private final Input input;

    /**
     * Target to save to.
     */
    private final Func<String, Output> target;

    /**
     * Ctor.
     * @param ipt Input text
     * @param dir Directory to write to
     */
    public Program(final Input ipt, final Path dir) {
        this(
            ipt,
            path -> new FileAsOutput(
                new File(dir.toFile(), path)
            )
        );
    }

    /**
     * Ctor.
     * @param ipt Input text
     * @param tgt Target
     */
    public Program(final Input ipt, final Func<String, Output> tgt) {
        this.input = ipt;
        this.target = tgt;
    }

    /**
     * Compile it to Java and save.
     * @throws IOException If fails
     */
    public void compile() throws IOException {
        final ANTLRErrorListener errors = new BaseErrorListener() {
            // @checkstyle ParameterNumberCheck (10 lines)
            @Override
            public void syntaxError(final Recognizer<?, ?> recognizer,
                final Object symbol, final int line,
                final int position, final String msg,
                final RecognitionException error) {
                throw new CompileException(error);
            }
        };
        final ProgramLexer lexer = new ProgramLexer(
            new ANTLRInputStream(this.input.stream())
        );
        lexer.addErrorListener(errors);
        final ProgramParser parser = new ProgramParser(
            new CommonTokenStream(lexer)
        );
        parser.addErrorListener(errors);
        final Tree tree = parser.program().ret;
        new IoCheckedScalar<>(
            new And(
                tree.java().entrySet(),
                path -> {
                    new LengthOfInput(
                        new TeeInput(
                            path.getValue(),
                            this.target.apply(path.getKey())
                        )
                    ).value();
                }
            )
        ).value();
    }

}
