package com.mpt.monio.auth.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@RedisHash("invalidated_token")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class InvalidatedToken {
    @Id
    private String id;

    @TimeToLive
    private long expTime; // lưu trữ để xóa sau 1 tgian nhất định -> cải thiện chi phí lưu trữ
}
