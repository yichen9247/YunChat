package com.server.yunchat.admin.man;

import com.server.yunchat.builder.model.SystemModel;

import java.util.HashMap;
import java.util.Map;

public class ServerSystemManage {

    public Map<String, Object> setSystemKeyStatus(SystemModel OUSystemModel, String value) {
        OUSystemModel.setValue(value);
        return new HashMap<>() {{
            put("status", OUSystemModel.getValue());
        }};
    }
}
