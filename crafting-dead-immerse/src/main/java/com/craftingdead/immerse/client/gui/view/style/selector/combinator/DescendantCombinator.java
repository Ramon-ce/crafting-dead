/*
 * Crafting Dead
 * Copyright (C) 2022  NexusNode LTD
 *
 * This Non-Commercial Software License Agreement (the "Agreement") is made between
 * you (the "Licensee") and NEXUSNODE (BRAD HUNTER). (the "Licensor").
 * By installing or otherwise using Crafting Dead (the "Software"), you agree to be
 * bound by the terms and conditions of this Agreement as may be revised from time
 * to time at Licensor's sole discretion.
 *
 * If you do not agree to the terms and conditions of this Agreement do not download,
 * copy, reproduce or otherwise use any of the source code available online at any time.
 *
 * https://github.com/nexusnode/crafting-dead/blob/1.18.x/LICENSE.txt
 *
 * https://craftingdead.net/terms.php
 */

package com.craftingdead.immerse.client.gui.view.style.selector.combinator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.craftingdead.immerse.client.gui.view.style.StyleNode;
import com.craftingdead.immerse.client.gui.view.style.selector.Selector;

/**
 * Implements behaviour for the CSS descendant and child combinators.
 * 
 * @see <a href="https://drafts.csswg.org/selectors/#descendant-combinators">
 *      https://drafts.csswg.org/selectors/#descendant-combinators</a>
 *      <a href="https://drafts.csswg.org/selectors/#child-combinators">
 *      https://drafts.csswg.org/selectors/#child-combinators</a>
 * 
 * @author Sm0keySa1m0n
 */
public class DescendantCombinator implements Combinator {

  private final boolean direct;

  public DescendantCombinator(boolean direct) {
    this.direct = direct;
  }

  @Override
  public Optional<Result> match(StyleNode node, Selector selector) {
    var parent = node.getParent();
    if (parent == null) {
      return Optional.empty();
    }

    if (this.direct) {
      return selector.match(parent).map(nodeStates -> new Result(parent, nodeStates));
    }

    var current = parent;
    Result result = null;
    while (current != null) {
      var currentResult = selector.match(current);
      if (currentResult.isPresent()) {
        result = new Result(current, currentResult.get());
      }
      current = current.getParent();
    }

    return Optional.ofNullable(result);
  }

  @Override
  public List<Result> reverseMatch(StyleNode node, Selector selector) {
    return this.matchChildren(node, selector);
  }

  private List<Result> matchChildren(StyleNode node, Selector selector) {
    var results = new ArrayList<Result>();
    for (var child : node.getChildStyles()) {
      selector.match(child).ifPresent(nodeStates -> results.add(new Result(child, nodeStates)));
      results.addAll(this.matchChildren(child, selector));
    }
    return results;
  }

  @Override
  public String toString() {
    return "Combinator[" + (this.direct ? ">" : " ") + "]";
  }
}