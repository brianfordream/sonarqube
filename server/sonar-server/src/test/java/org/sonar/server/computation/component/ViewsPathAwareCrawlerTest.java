/*
 * SonarQube, open source software quality management tool.
 * Copyright (C) 2008-2014 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * SonarQube is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * SonarQube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.server.computation.component;

import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import org.junit.Test;

import static com.google.common.collect.ImmutableList.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.sonar.server.computation.component.Component.Type.PROJECT_VIEW;
import static org.sonar.server.computation.component.Component.Type.SUBVIEW;
import static org.sonar.server.computation.component.Component.Type.VIEW;
import static org.sonar.server.computation.component.ComponentVisitor.Order.POST_ORDER;
import static org.sonar.server.computation.component.ComponentVisitor.Order.PRE_ORDER;

public class ViewsPathAwareCrawlerTest {

  private static final int ROOT_KEY = 1;
  private static final Component SOME_TREE_ROOT = ViewsComponent.builder(VIEW, ROOT_KEY)
    .addChildren(
        ViewsComponent.builder(SUBVIEW, 11)
        .addChildren(
            ViewsComponent.builder(SUBVIEW, 111)
            .addChildren(
                ViewsComponent.builder(PROJECT_VIEW, 1111).build(),
                ViewsComponent.builder(PROJECT_VIEW, 1112).build()
            )
            .build(),
            ViewsComponent.builder(SUBVIEW, 112)
            .addChildren(
              ViewsComponent.builder(PROJECT_VIEW, 1121).build()
            )
            .build())
        .build(),
        ViewsComponent.builder(SUBVIEW, 12)
        .addChildren(
            ViewsComponent.builder(SUBVIEW, 121)
            .addChildren(
                ViewsComponent.builder(SUBVIEW, 1211)
                .addChildren(
                    ViewsComponent.builder(PROJECT_VIEW, 12111).build()
                )
                .build()
            ).build()
        ).build()
    ).build();

  @Test
  public void verify_preOrder_visit_call_when_visit_tree_with_depth_PROJECT_VIEW() {
    TestPathAwareCrawler underTest = new TestPathAwareCrawler(PROJECT_VIEW, PRE_ORDER);
    underTest.visit(SOME_TREE_ROOT);

    Iterator<CallRecord> expected = of(
      viewsCallRecord("visitAny", 1, null, of(1)),
      viewsCallRecord("visitView", 1, null, of(1)),
      viewsCallRecord("visitAny", 11, 1, of(11, 1)),
      viewsCallRecord("visitSubView", 11, 1, of(11, 1)),
      viewsCallRecord("visitAny", 111, 11, of(111, 11, 1)),
      viewsCallRecord("visitSubView", 111, 11, of(111, 11, 1)),
      viewsCallRecord("visitAny", 1111, 111, of(1111, 111, 11, 1)),
      viewsCallRecord("visitProjectView", 1111, 111, of(1111, 111, 11, 1)),
      viewsCallRecord("visitAny", 1112, 111, of(1112, 111, 11, 1)),
      viewsCallRecord("visitProjectView", 1112, 111, of(1112, 111, 11, 1)),
      viewsCallRecord("visitAny", 112, 11, of(112, 11, 1)),
      viewsCallRecord("visitSubView", 112, 11, of(112, 11, 1)),
      viewsCallRecord("visitAny", 1121, 112, of(1121, 112, 11, 1)),
      viewsCallRecord("visitProjectView", 1121, 112, of(1121, 112, 11, 1)),
      viewsCallRecord("visitAny", 12, 1, of(12, 1)),
      viewsCallRecord("visitSubView", 12, 1, of(12, 1)),
      viewsCallRecord("visitAny", 121, 12, of(121, 12, 1)),
      viewsCallRecord("visitSubView", 121, 12, of(121, 12, 1)),
      viewsCallRecord("visitAny", 1211, 121, of(1211, 121, 12, 1)),
      viewsCallRecord("visitSubView", 1211, 121, of(1211, 121, 12, 1)),
      viewsCallRecord("visitAny", 12111, 1211, of(12111, 1211, 121, 12, 1)),
      viewsCallRecord("visitProjectView", 12111, 1211, of(12111, 1211, 121, 12, 1))
      ).iterator();
    verifyCallRecords(expected, underTest.callsRecords.iterator());
  }

  @Test
  public void verify_preOrder_visit_call_when_visit_tree_with_depth_SUBVIEW() {
    TestPathAwareCrawler underTest = new TestPathAwareCrawler(SUBVIEW, PRE_ORDER);
    underTest.visit(SOME_TREE_ROOT);

    Iterator<CallRecord> expected = of(
      viewsCallRecord("visitAny", 1, null, of(1)),
      viewsCallRecord("visitView", 1, null, of(1)),
      viewsCallRecord("visitAny", 11, 1, of(11, 1)),
      viewsCallRecord("visitSubView", 11, 1, of(11, 1)),
      viewsCallRecord("visitAny", 111, 11, of(111, 11, 1)),
      viewsCallRecord("visitSubView", 111, 11, of(111, 11, 1)),
      viewsCallRecord("visitAny", 112, 11, of(112, 11, 1)),
      viewsCallRecord("visitSubView", 112, 11, of(112, 11, 1)),
      viewsCallRecord("visitAny", 12, 1, of(12, 1)),
      viewsCallRecord("visitSubView", 12, 1, of(12, 1)),
      viewsCallRecord("visitAny", 121, 12, of(121, 12, 1)),
      viewsCallRecord("visitSubView", 121, 12, of(121, 12, 1)),
      viewsCallRecord("visitAny", 1211, 121, of(1211, 121, 12, 1)),
      viewsCallRecord("visitSubView", 1211, 121, of(1211, 121, 12, 1))
      ).iterator();
    verifyCallRecords(expected, underTest.callsRecords.iterator());
  }

  @Test
  public void verify_preOrder_visit_call_when_visit_tree_with_depth_VIEW() {
    TestPathAwareCrawler underTest = new TestPathAwareCrawler(VIEW, PRE_ORDER);
    underTest.visit(SOME_TREE_ROOT);

    Iterator<CallRecord> expected = of(
      viewsCallRecord("visitAny", 1, null, of(1)),
      viewsCallRecord("visitView", 1, null, of(1))
      ).iterator();
    verifyCallRecords(expected, underTest.callsRecords.iterator());
  }

  @Test
  public void verify_postOrder_visit_call_when_visit_tree_with_depth_PROJECT_VIEW() {
    TestPathAwareCrawler underTest = new TestPathAwareCrawler(PROJECT_VIEW, POST_ORDER);
    underTest.visit(SOME_TREE_ROOT);

    Iterator<CallRecord> expected = of(
      viewsCallRecord("visitAny", 1111, 111, of(1111, 111, 11, 1)),
      viewsCallRecord("visitProjectView", 1111, 111, of(1111, 111, 11, 1)),
      viewsCallRecord("visitAny", 1112, 111, of(1112, 111, 11, 1)),
      viewsCallRecord("visitProjectView", 1112, 111, of(1112, 111, 11, 1)),
      viewsCallRecord("visitAny", 111, 11, of(111, 11, 1)),
      viewsCallRecord("visitSubView", 111, 11, of(111, 11, 1)),
      viewsCallRecord("visitAny", 1121, 112, of(1121, 112, 11, 1)),
      viewsCallRecord("visitProjectView", 1121, 112, of(1121, 112, 11, 1)),
      viewsCallRecord("visitAny", 112, 11, of(112, 11, 1)),
      viewsCallRecord("visitSubView", 112, 11, of(112, 11, 1)),
      viewsCallRecord("visitAny", 11, 1, of(11, 1)),
      viewsCallRecord("visitSubView", 11, 1, of(11, 1)),
      viewsCallRecord("visitAny", 12111, 1211, of(12111, 1211, 121, 12, 1)),
      viewsCallRecord("visitProjectView", 12111, 1211, of(12111, 1211, 121, 12, 1)),
      viewsCallRecord("visitAny", 1211, 121, of(1211, 121, 12, 1)),
      viewsCallRecord("visitSubView", 1211, 121, of(1211, 121, 12, 1)),
      viewsCallRecord("visitAny", 121, 12, of(121, 12, 1)),
      viewsCallRecord("visitSubView", 121, 12, of(121, 12, 1)),
      viewsCallRecord("visitAny", 12, 1, of(12, 1)),
      viewsCallRecord("visitSubView", 12, 1, of(12, 1)),
      viewsCallRecord("visitAny", 1, null, of(1)),
      viewsCallRecord("visitView", 1, null, of(1))
      ).iterator();
    verifyCallRecords(expected, underTest.callsRecords.iterator());
  }

  @Test
  public void verify_postOrder_visit_call_when_visit_tree_with_depth_SUBVIEW() {
    TestPathAwareCrawler underTest = new TestPathAwareCrawler(SUBVIEW, POST_ORDER);
    underTest.visit(SOME_TREE_ROOT);

    Iterator<CallRecord> expected = of(
      viewsCallRecord("visitAny", 111, 11, of(111, 11, 1)),
      viewsCallRecord("visitSubView", 111, 11, of(111, 11, 1)),
      viewsCallRecord("visitAny", 112, 11, of(112, 11, 1)),
      viewsCallRecord("visitSubView", 112, 11, of(112, 11, 1)),
      viewsCallRecord("visitAny", 11, 1, of(11, 1)),
      viewsCallRecord("visitSubView", 11, 1, of(11, 1)),
      viewsCallRecord("visitAny", 1211, 121, of(1211, 121, 12, 1)),
      viewsCallRecord("visitSubView", 1211, 121, of(1211, 121, 12, 1)),
      viewsCallRecord("visitAny", 121, 12, of(121, 12, 1)),
      viewsCallRecord("visitSubView", 121, 12, of(121, 12, 1)),
      viewsCallRecord("visitAny", 12, 1, of(12, 1)),
      viewsCallRecord("visitSubView", 12, 1, of(12, 1)),
      viewsCallRecord("visitAny", 1, null, of(1)),
      viewsCallRecord("visitView", 1, null, of(1))
      ).iterator();
    verifyCallRecords(expected, underTest.callsRecords.iterator());
  }

  @Test
  public void verify_postOrder_visit_call_when_visit_tree_with_depth_VIEW() {
    TestPathAwareCrawler underTest = new TestPathAwareCrawler(VIEW, POST_ORDER);
    underTest.visit(SOME_TREE_ROOT);

    Iterator<CallRecord> expected = of(
      viewsCallRecord("visitAny", 1, null, of(1)),
      viewsCallRecord("visitView", 1, null, of(1))
      ).iterator();
    verifyCallRecords(expected, underTest.callsRecords.iterator());
  }

  private static void verifyCallRecords(Iterator<CallRecord> expected, Iterator<CallRecord> actual) {
    int i = 1;
    while (expected.hasNext()) {
      assertThat(actual.next()).describedAs(String.format("Expected call n°%s does not match actual call n°%s", i, i)).isEqualTo(expected.next());
      i++;
    }
    assertThat(expected.hasNext()).isEqualTo(actual.hasNext());
  }

  private static CallRecord viewsCallRecord(String method, int currentRef, @Nullable Integer parentRef, List<Integer> path) {
    return CallRecord.viewsCallRecord(method, String.valueOf(currentRef), currentRef, parentRef, ROOT_KEY, path);
  }

}
