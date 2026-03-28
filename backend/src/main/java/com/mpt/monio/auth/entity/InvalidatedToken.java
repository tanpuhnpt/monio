package com.mpt.monio.auth.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class InvalidatedToken {
    @Id
    private String id;

    private long expTime; // lưu trữ để xóa sau 1 tgian nhất định -> cải thiện chi phí lưu trữ
}
