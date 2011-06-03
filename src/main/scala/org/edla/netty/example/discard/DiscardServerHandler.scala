package org.edla.netty.example.discard

import java.util.concurrent.atomic.AtomicLong
import org.jboss.netty.buffer.ChannelBuffer
import org.jboss.netty.channel.{
  ChannelEvent,
  ChannelHandlerContext,
  ChannelStateEvent,
  ExceptionEvent,
  MessageEvent,
  SimpleChannelUpstreamHandler
}
import java.util.logging.Logger

class DiscardServerHandler extends SimpleChannelUpstreamHandler {

  val logger = Logger.getLogger(getClass.getName)

  val transferredBytes = new AtomicLong

  def getTransferredBytes: Long = {
    transferredBytes.get
  }

  override def handleUpstream(ctx: ChannelHandlerContext, e: ChannelEvent): Unit = {
    e match {
      case c: ChannelStateEvent => logger.info(e.toString)
      case _ => None
    }
    super.handleUpstream(ctx, e)
  }

  override def messageReceived(ctx: ChannelHandlerContext, e: MessageEvent): Unit = {
    // Discard received data silently by doing nothing.
    transferredBytes.addAndGet((e.getMessage match {
      case c: ChannelBuffer => c
      case _ => throw new ClassCastException
    }) readableBytes)
  }

  override def exceptionCaught(context: ChannelHandlerContext, e: ExceptionEvent): Unit = {
    // Close the connection when an exception is raised.
    logger.warning("Unexpected exception from downstream." + e.getCause)
    e.getChannel.close
  }

}