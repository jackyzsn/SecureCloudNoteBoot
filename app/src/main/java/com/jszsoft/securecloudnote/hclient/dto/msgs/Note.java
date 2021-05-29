
package com.jszsoft.securecloudnote.hclient.dto.msgs;

import java.io.Serializable;
import lombok.Data;

@Data
public class Note implements Serializable
{

    private String id;

    private String noteId;

    private String noteUserId;

    private String noteTimeTag;

    private String noteContent;
    private static final long serialVersionUID = 1397887086330541674L;

 
}
