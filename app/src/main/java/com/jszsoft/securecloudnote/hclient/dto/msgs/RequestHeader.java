
package com.jszsoft.securecloudnote.hclient.dto.msgs;

import java.io.Serializable;

import lombok.Data;

@Data
public class RequestHeader implements Serializable
{

    private String rqUID;

    private String systemID;

    private String userID;

    private String token;
    private static final long serialVersionUID = -4928059178865286342L;

}
