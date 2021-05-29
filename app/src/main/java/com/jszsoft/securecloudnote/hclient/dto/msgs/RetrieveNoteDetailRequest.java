
package com.jszsoft.securecloudnote.hclient.dto.msgs;

import java.io.Serializable;
import lombok.Data;

@Data
public class RetrieveNoteDetailRequest implements Serializable
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 2024377870888567304L;

	private RequestHeader requestHeader;

    private String id;


}
