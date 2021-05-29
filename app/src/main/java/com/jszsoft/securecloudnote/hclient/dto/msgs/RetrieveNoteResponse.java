
package com.jszsoft.securecloudnote.hclient.dto.msgs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class RetrieveNoteResponse implements Serializable
{
    private ResponseHeader responseHeader;

    private List<Note> noteList = new ArrayList<>();
    private static final long serialVersionUID = 5570815748108616300L;
}
