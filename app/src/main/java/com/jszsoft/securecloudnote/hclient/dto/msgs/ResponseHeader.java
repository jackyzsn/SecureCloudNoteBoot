
package com.jszsoft.securecloudnote.hclient.dto.msgs;

import java.io.Serializable;
import lombok.Data;

@Data
public class ResponseHeader implements Serializable
{


    private String rqUID;

    private String systemID;

    private String userID;

    private String statusCode;

    private String statusDesc;
    private static final long serialVersionUID = -1815960380406352760L;

}
