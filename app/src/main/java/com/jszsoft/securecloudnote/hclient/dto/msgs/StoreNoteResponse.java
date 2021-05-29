
package com.jszsoft.securecloudnote.hclient.dto.msgs;

import java.io.Serializable;
import lombok.Data;

@Data
public class StoreNoteResponse implements Serializable
{
    private ResponseHeader responseHeader;

    private static final long serialVersionUID = -3297761737920467792L;
}
