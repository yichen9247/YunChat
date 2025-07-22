package com.server.yunchat.upload.man;

import com.server.yunchat.builder.model.UploadModel;
import com.server.yunchat.builder.utils.HandUtils;
import com.server.yunchat.builder.utils.IDGenerator;

public class UploadManage {
    private final HandUtils handUtils;
    private final IDGenerator idGenerator;

    public UploadManage(HandUtils handUtils, IDGenerator idGenerator) {
        this.handUtils = handUtils;
        this.idGenerator = idGenerator;
    }

    public String insertUploadFile(UploadModel uploadModel, Long uid, String name, String path, String time, String type, Long size) {
        String fid = idGenerator.generateRandomFileId(uid, name, path, time);
        uploadModel.setFid(fid);
        uploadModel.setUid(uid);
        uploadModel.setName(name);
        uploadModel.setPath(path);
        uploadModel.setType(type);
        uploadModel.setSize(size);
        uploadModel.setTime(handUtils.formatTimeForString("yyyy-MM-dd HH:mm:ss"));
        return fid;
    }
}
