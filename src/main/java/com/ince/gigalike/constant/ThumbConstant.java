package com.ince.gigalike.constant;

public interface ThumbConstant {

    String USER_THUMB_KEY_PREFIX = "thumb:";

    String TEMP_THUMB_KEY_PREFIX = "thumb:temp:%s";

    /**
     * 如果用户某个点赞信息已在本地缓存，用户此时取消点赞，如果不处理本地缓存，就会导致本地缓存和redis数据不一致，但是又不能直接从本地缓存删除，濒危他毕竟是超级热点数据。
     * 所以我们约定，当值为0时，代表当前未点赞。
     */
    Long UN_THUMB_CONSTANT = 0L;



}
