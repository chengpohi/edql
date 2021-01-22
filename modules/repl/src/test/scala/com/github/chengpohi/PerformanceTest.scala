package com.github.chengpohi

import java.util.concurrent.Semaphore
import java.util.concurrent.atomic.AtomicLong

import com.github.chengpohi.helper.EQLTestTrait

import scala.util.{Failure, Success}

/**
  * Created by xiachen on 31/12/2016.
  */
class PerformanceTest extends EQLTestTrait {
  import com.github.chengpohi.helper.EQLTestContext._

  ignore should "pressure test" in {
    val permits = 1000
    val waitTime = 1 * 1000
    val semaphore = new Semaphore(permits)
    val millis: Long = System.currentTimeMillis()
    val long = new AtomicLong(millis)
    Stream
      .from(1)
      .foreach(i => {
        if (semaphore.availablePermits() == 0) {
          while ((System.currentTimeMillis() - long.get()) < waitTime) {}
          semaphore.acquire(permits)
          long.set(System.currentTimeMillis())
          semaphore.release(permits)
        }
        semaphore.acquire()
        println("index id: " + i)
        EQL {
          index into "testindex" / "testmap" doc Map("Hello" -> List(
            "world",
            "&lt;p&gt;I want to use a track-bar to change a form's opacity.&lt;/p&gt;&#xA;&#xA;&lt;p&gt;This is my code:&lt;/p&gt;&#xA;&#xA;&lt;pre&gt;&lt;code&gt;decimal trans = trackBar1.Value / 5000;&#xA;this.Opacity = trans;&#xA;&lt;/code&gt;&lt;/pre&gt;&#xA;&#xA;&lt;p&gt;When I try to build it, I get this error:&lt;/p&gt;&#xA;&#xA;&lt;blockquote&gt;&#xA;  &lt;p&gt;Cannot implicitly convert type 'decimal' to 'double'.&lt;/p&gt;&#xA;&lt;/blockquote&gt;&#xA;&#xA;&lt;p&gt;I tried making &lt;code&gt;trans&lt;/code&gt; a &lt;code&gt;double&lt;/code&gt;, but then the control doesn't work. This code has worked fine for me in VB.NET in the past. &lt;/p&gt;&#xA;"
          )) id i.toString
        }.onComplete {
          case Success(s) => {
            println("success " + s.getId)
            semaphore.release()
          }
          case Failure(e) => {
            println("failure " + e.getMessage)
            semaphore.release()
          }
        }
      })
  }
}
