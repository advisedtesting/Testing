/*
 * The MIT License
 * Copyright © 2016 AdvisedTesting
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.github.advisedtesting.logback;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.Test;

import com.github.advisedtesting.logback.internal.Wait;

public class WaitTest {

  @Test
  public void failingWaitTest() {
    ThrowingCallable callable = () -> new Wait<>().on(() -> {
      throw new IllegalStateException("failed");
    }).trying(2).toComplete();
    assertThatThrownBy(callable).hasMessage("failed").isInstanceOf(IllegalStateException.class);
  }

  @Test
  public void nullingOutWaitTest() {
    assertThat(new Wait<>().on(() -> null).trying(3).toComplete()).isNull();
  }
  
  
  @Test
  public void simulateInterruptWaitTest() throws InterruptedException, ExecutionException {
    Runnable runnable = () -> new Wait<>().on(() -> null).trying(20).toComplete();
    ExecutorService ex = Executors.newSingleThreadExecutor(); 
    Future<?> future = ex.submit(runnable);
    ex.shutdownNow();
    assertThatThrownBy(() -> future.get())
        .hasCauseExactlyInstanceOf(CompletionException.class)
        .hasRootCauseExactlyInstanceOf(InterruptedException.class);   
  }
}
