
package com.jszsoft.securecloudnote.hclient.dto.msgs;

import java.io.Serializable;
import lombok.Data;

@Data
public class RetrieveNoteRequest implements Serializable
{

    private RequestHeader requestHeader;

    private String noteId;

    private String noteUserId;
    private static final long serialVersionUID = -5323092379747978666L;

}
