/*
 * SonarQube JavaScript Plugin
 * Copyright (C) 2011-2017 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.javascript.checks;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import org.sonar.check.Rule;
import org.sonar.plugins.javascript.api.tree.Tree;
import org.sonar.plugins.javascript.api.tree.Tree.Kind;
import org.sonar.plugins.javascript.api.tree.expression.LiteralTree;
import org.sonar.plugins.javascript.api.tree.statement.ExpressionStatementTree;
import org.sonar.plugins.javascript.api.visitors.DoubleDispatchVisitorCheck;

@Rule(key = "S905")
public class UselessExpressionStatementCheck extends DoubleDispatchVisitorCheck {

  private static final String MESSAGE = "Refactor or remove this statement.";

  private static final Set<String> KNOWN_DIRECTIVES = ImmutableSet.of("use strict", "$:nomunge", "ngInject");

  @Override
  public void visitExpressionStatement(ExpressionStatementTree tree) {
    Tree expression = tree.expression();

    if (expression.is(Kind.EQUAL_TO)) {
      addIssue(tree, MESSAGE);
    }

    if (expression.is(Kind.STRING_LITERAL) && !isDirective((LiteralTree) expression)) {
      addIssue(tree, MESSAGE);
    }

    super.visitExpressionStatement(tree);
  }

  private static boolean isDirective(LiteralTree tree) {
    if (tree.is(Kind.STRING_LITERAL)) {
      return KNOWN_DIRECTIVES.contains(trimQuotes((tree).value()));
    }

    return false;
  }

  private static String trimQuotes(String value) {
    return value.substring(1, value.length() - 1);
  }
}
