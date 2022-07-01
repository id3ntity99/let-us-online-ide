package com.letus.websocket;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.letus.tokenauth.Auth;
import com.letus.user.Profile;
import com.letus.user.User;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.CharsetUtil;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

public class AuthHandler extends SimpleChannelInboundHandler<HttpRequest> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Map<Channel, User> chanUserHash;
    private final Map<String, User> idUserHash;

    public AuthHandler(Map<Channel, User> userHashMap, Map<String, User> idUserHash) {
        super();
        this.chanUserHash = userHashMap;
        this.idUserHash = idUserHash;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, HttpRequest req) {
        // TODO Frontend encrypts id token with public key,
        //  server decrypts the id token with private key.
        QueryStringDecoder decoder = new QueryStringDecoder(req.uri());
        String idToken = decoder.parameters().get("id_token").get(0);
        Profile profile;
        try {
            profile = verify(idToken);
            System.out.println(idUserHash);
            logger.debug("New user has been connected...Build new user");
            if (!idUserHash.containsKey(profile.getUserId())) {
                ctx.pipeline().fireUserEventTriggered(profile);
                ctx.fireChannelRead(req);
                ctx.pipeline().remove(this);
            } else {
                System.out.println(idUserHash);
                logger.debug("User exists... Initiating handshake");
                User user = idUserHash.get(profile.getUserId());
                Channel oldChannel = user.getChannel();
                user.updateChannel(ctx.channel());
                chanUserHash.remove(oldChannel);
                chanUserHash.put(user.getChannel(), user);
                ctx.fireChannelRead(req);
                ctx.pipeline().remove(this);
            }
        } catch (Exception e) {
            logger.warn("Exception raised while handling authentication, and sent a 400 BAD_REQUEST response to the client", e);
            JSONObject jsonObject = new JSONObject().append("auth_success", false);
            ByteBuf content = Unpooled.copiedBuffer(jsonObject.toString(), CharsetUtil.UTF_8);
            HttpResponse response = new Response.ResponseBuilder()
                    .withContent(content)
                    .withStatus(HttpResponseStatus.BAD_REQUEST)
                    .withContentType("application/json")
                    .build()
                    .getHttpResponse();
            ctx.writeAndFlush(response);
        }
    }

    public Profile verify(String idToken) throws GeneralSecurityException, IOException {
        GoogleIdToken.Payload payload = Auth.verify(idToken);
        return new Profile(payload);
    }
}
