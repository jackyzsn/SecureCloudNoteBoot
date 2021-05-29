
package com.jszsoft.securecloudnote.hclient.dto.msgs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class RetrieveNoteDetailResponse implements Serializable
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 8654732072653947442L;

	private ResponseHeader responseHeader;

    private Note note;

}
