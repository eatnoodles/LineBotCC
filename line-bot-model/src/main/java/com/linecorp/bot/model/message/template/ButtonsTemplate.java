/*
 * Copyright (c) 2016 LINE Corporation. All rights reserved.
 * LINE Corporation PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.linecorp.bot.model.message.template;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import com.linecorp.bot.model.action.Action;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
@JsonTypeName("buttons")
public class ButtonsTemplate implements Template {
    private final String thumbnailImageUrl;
    private final String title;
    private final String text;
    private final List<Action> actions;

    @JsonCreator
    public ButtonsTemplate(
            @JsonProperty("thumbnailImageUrl") String thumbnailImageUrl,
            @JsonProperty("title") String title,
            @JsonProperty("text") String text,
            @JsonProperty("actions") List<Action> actions) {
        this.thumbnailImageUrl = thumbnailImageUrl;
        this.title = title;
        this.text = text;
        this.actions = actions != null ? actions : Collections.emptyList();
    }
}