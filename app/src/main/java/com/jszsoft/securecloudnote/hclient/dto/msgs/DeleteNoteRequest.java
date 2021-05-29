
package com.jszsoft.securecloudnote.hclient.dto.msgs;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class DeleteNoteRequest implements Serializable
{

    private RequestHeader requestHeader;
    
    private String deleteKey;
    
    private List<String> ids;
    
    private static final long serialVersionUID = -5323092379747978666L;

}
