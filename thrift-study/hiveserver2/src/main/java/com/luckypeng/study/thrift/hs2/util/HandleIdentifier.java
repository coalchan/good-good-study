package com.luckypeng.study.thrift.hs2.util;

import lombok.Getter;
import org.apache.hive.service.cli.thrift.THandleIdentifier;

import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * 来自于 hive-service.jar 中的 org.apache.hive.service.cli 包下，可以用于对 THandleIdentifier 的 byte 数据进行反序列化
 * @author coalchan
 * @date 2020/02/20
 */
public class HandleIdentifier {
    private final UUID publicId;
    private final UUID secretId;
    @Getter
    private String queryId;
    public HandleIdentifier() {
        publicId = UUID.randomUUID();
        secretId = UUID.randomUUID();
    }
    public HandleIdentifier(UUID publicId, UUID secretId) {
        this.publicId = publicId;
        this.secretId = secretId;
    }
    public HandleIdentifier(THandleIdentifier tHandleId) {
        byte[] guidBytes = tHandleId.getGuid();
        ByteBuffer bb = ByteBuffer.wrap(guidBytes);
        this.publicId = new UUID(bb.getLong(), bb.getLong());
        bb = ByteBuffer.wrap(tHandleId.getSecret());
        this.secretId = new UUID(bb.getLong(), bb.getLong());
        this.queryId = extractQueryId(guidBytes);
    }
    public UUID getPublicId() {
        return publicId;
    }
    public UUID getSecretId() {
        return secretId;
    }
    public THandleIdentifier toTHandleIdentifier() {
        byte[] guid = new byte[16];
        byte[] secret = new byte[16];
        ByteBuffer guidBB = ByteBuffer.wrap(guid);
        ByteBuffer secretBB = ByteBuffer.wrap(secret);
        guidBB.putLong(publicId.getMostSignificantBits());
        guidBB.putLong(publicId.getLeastSignificantBits());
        secretBB.putLong(secretId.getMostSignificantBits());
        secretBB.putLong(secretId.getLeastSignificantBits());
        return new THandleIdentifier(ByteBuffer.wrap(guid), ByteBuffer.wrap(secret));
    }

    /**
     * 对于 Impala Query，可以通过 guid 或者 secret（二者值一样） 来得到 queryId.
     * 按照小端字节序每隔8个字节进行读取，中间使用冒号进行分隔
     * @see org.apache.thrift.TBaseHelper#toString
     * @param guid
     * @return
     */
    private static final int BLOCK = 8;
    private static final char SPLIT = ':';
    public String extractQueryId(byte[] guid) {
        if(guid == null) {return "";}
        StringBuilder sb = new StringBuilder();
        double blockCnt = Math.ceil(guid.length / BLOCK);
        for (int i = 0; i < blockCnt; i++) {
            int index = guid.length<=BLOCK*(i+1) ? (guid.length-1) : (BLOCK*(i+1)-1);
            for (int j = 0; j < BLOCK && index >= BLOCK*i; j++) {
                sb.append(paddedByteString(guid[index--]));
            }
            if (i < blockCnt - 1) {
                sb.append(SPLIT);
            }
        }
        return sb.toString();
    }

    private static String paddedByteString(byte b) {
        int extended = (b | 0x100) & 0x1ff;
        return Integer.toHexString(extended).toLowerCase().substring(1);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((publicId == null) ? 0 : publicId.hashCode());
        result = prime * result + ((secretId == null) ? 0 : secretId.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof HandleIdentifier)) {
            return false;
        }
        HandleIdentifier other = (HandleIdentifier) obj;
        if (publicId == null) {
            if (other.publicId != null) {
                return false;
            }
        } else if (!publicId.equals(other.publicId)) {
            return false;
        }
        if (secretId == null) {
            if (other.secretId != null) {
                return false;
            }
        } else if (!secretId.equals(other.secretId)) {
            return false;
        }
        return true;
    }
    @Override
    public String toString() {
        return publicId.toString();
    }
}
