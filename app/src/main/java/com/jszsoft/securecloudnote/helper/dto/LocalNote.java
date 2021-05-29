package com.jszsoft.securecloudnote.helper.dto;

import lombok.Data;

@Data
public class LocalNote {

    private String id;

    private String noteId;

    private String noteUserId;

    private String noteTimeTag;

    private String noteContent;

    private String deleteKey;
}
