package client.docker.request;

import client.docker.dockerclient.exceptions.DockerResponseException;
import client.docker.model.Container;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Promise;

class StartContainerHandler extends DockerResponseHandler {
    public StartContainerHandler(Container container, DockerRequest nextRequest, Promise<Container> promise) {
        super(container, nextRequest, promise);
    }

    private void handleResponse(ChannelHandlerContext ctx) throws Exception {
        if (nextRequest != null) {
            logger.debug("Next request detected: {}", nextRequest.getClass().getSimpleName());
            nextRequest.setContainer(container).setPromise(promise);
            FullHttpRequest req = nextRequest.render();
            ctx.channel().writeAndFlush(req).addListener(new NextRequestListener(logger, ctx, nextRequest));
        } else {
            logger.info("There are no more requests... removing {}", this.getClass().getSimpleName());
            promise.setSuccess(container);
            ctx.pipeline().remove(this);
        }
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpResponse res) throws Exception {
        if (res.status().code() == 204) {
            handleResponse(ctx);
        } else {
            String errMessage = String.format("Unsuccessful response detected: %s %s",
                    res.status().toString(),
                    res.content().toString(CharsetUtil.UTF_8));
            throw new DockerResponseException(errMessage);
        }
    }
}
