package com.server.handsock.socket.listener;

import com.server.handsock.common.props.HandProp;
import com.server.handsock.common.utils.GlobalService;
import com.server.handsock.common.utils.HandUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Service
public class CheckListener {

    private final HandProp handProp;

    @Autowired
    public CheckListener(HandProp handProp) {
        this.handProp = handProp;
    }

    public void addEventListener(HandUtils handUtils) {
        Objects.requireNonNull(GlobalService.INSTANCE.getSocketIOServer()).addEventListener("[CLIENT:CHECK]", Map.class, (client, data, ackSender) -> {
            if (data.get("version").equals(handProp.getAppVersion())) {
                ackSender.sendAckData(handUtils.handleResultByCode(HttpStatus.OK, null, "服务正常"));
            } else ackSender.sendAckData(handUtils.handleResultByCode(HttpStatus.INTERNAL_SERVER_ERROR, null, "服务异常"));
        });
    }
}