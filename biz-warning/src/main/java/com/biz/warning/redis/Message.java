package com.biz.warning.redis;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private String userId;
    private Object content;

    @Override
    public String toString() {
        return userId + "|" + (content == null ? "null" : content.toString());
    }
}
