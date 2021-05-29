
package com.jszsoft.securecloudnote.hclient.dto.msgs;

import java.io.Serializable;
import lombok.Data;

@Data
public class StoreNoteRequest implements Serializable
{
    private RequestHeader requestHeader;

    private String noteId;

    private String noteUserId;

    private String noteTimeTag;

    private String noteContent;
    
    private String deleteKey;
    
    private static final long serialVersionUID = -6754967855492197222L;

}
