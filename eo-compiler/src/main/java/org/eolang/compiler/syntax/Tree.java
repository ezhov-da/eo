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
package org.eolang.compiler.syntax;

import java.util.Collection;
import java.util.Map;
import org.cactoos.Input;
import org.cactoos.list.IterableAsMap;
import org.cactoos.list.MapEntry;
import org.cactoos.list.MappedIterable;

/**
 * AST.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.1
 */
public final class Tree {

    /**
     * Root nodes.
     */
    private final Collection<RootNode> nodes;

    /**
     * Ctor.
     * @param nodes All AST root nodes.
     */
    public Tree(final Collection<RootNode> nodes) {
        this.nodes = nodes;
    }

    /**
     * Compile it to Java files.
     * @return Java files (path, content)
     */
    public Map<String, Input> java() {
        return new IterableAsMap<>(
            new MappedIterable<>(
                new MappedIterable<>(this.nodes, RootNode::java),
                javaFile -> new MapEntry<>(
                    javaFile.path(), javaFile.code()
                )
            )
        );
    }
}
