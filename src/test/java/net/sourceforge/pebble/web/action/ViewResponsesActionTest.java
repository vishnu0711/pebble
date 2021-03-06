/*
 * Copyright (c) 2003-2011, Simon Brown
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in
 *     the documentation and/or other materials provided with the
 *     distribution.
 *
 *   - Neither the name of Pebble nor the names of its contributors may
 *     be used to endorse or promote products derived from this software
 *     without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package net.sourceforge.pebble.web.action;

import net.sourceforge.pebble.Constants;
import net.sourceforge.pebble.domain.BlogEntry;
import net.sourceforge.pebble.domain.Comment;
import net.sourceforge.pebble.domain.BlogService;
import net.sourceforge.pebble.util.Pageable;
import net.sourceforge.pebble.web.view.View;
import net.sourceforge.pebble.web.view.impl.ResponsesView;

/**
 * Tests for the ViewResponsesAction class.
 *
 * @author    Simon Brown
 */
public class ViewResponsesActionTest extends SecureActionTestCase {

  protected void setUp() throws Exception {
    action = new ViewResponsesAction();

    super.setUp();
  }

  /**
   * Test that only blog contributors can manage responses.
   */
  public void testOnlyBlogContributorsHaveAccess() {
    String roles[] = action.getRoles(request);
    assertEquals(1, roles.length);
    assertEquals(Constants.BLOG_CONTRIBUTOR_ROLE, roles[0]);
  }

  public void testActionCalledWithDefaultParameters() throws Exception {
    View view = action.process(request, response);
    assertTrue(view instanceof ResponsesView);

    assertEquals("approved", model.get("type"));
    Pageable pageable = (Pageable)model.get("pageable");
    assertNotNull("pageable", pageable);
    assertEquals(0, pageable.getList().size());
    assertEquals(0, pageable.getPage());
    assertEquals(0, pageable.getMaxPages());
    assertEquals(0, pageable.getPreviousPage());
    assertEquals(0, pageable.getNextPage());
  }

  public void testActionCalledWithDefaultParametersAndLessThanAPageOfResponses() throws Exception {
    int numberOfComments = ViewResponsesAction.PAGE_SIZE - 1;
    BlogService service = new BlogService();
    BlogEntry blogEntry = new BlogEntry(blog);
    service.putBlogEntry(blogEntry);
    blogEntry = service.getBlogEntry(blog, blogEntry.getId());

    for (int i = 0; i < numberOfComments; i++) {
      Comment comment = blogEntry.createComment("title", "body"+i, "author", "email", "website", "avatar", "127.0.0.1");
      blogEntry.addComment(comment);
    }
    service.putBlogEntry(blogEntry);

    for (Comment comment : blogEntry.getComments()) {
      comment.setApproved();
    }
    service.putBlogEntry(blogEntry);
    

    View view = action.process(request, response);
    assertTrue(view instanceof ResponsesView);

    assertEquals("approved", model.get("type"));
    Pageable pageable = (Pageable)model.get("pageable");
    assertNotNull("pageable", pageable);
    assertEquals(numberOfComments, pageable.getList().size());
    assertEquals(1, pageable.getPage());
    assertEquals(1, pageable.getMaxPages());
    assertEquals(0, pageable.getPreviousPage());
    assertEquals(0, pageable.getNextPage());
  }

}