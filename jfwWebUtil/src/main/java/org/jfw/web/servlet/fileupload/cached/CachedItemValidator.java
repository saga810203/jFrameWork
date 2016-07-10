package org.jfw.web.servlet.fileupload.cached;

import java.util.List;

import org.jfw.util.exception.JfwBaseException;

public interface CachedItemValidator {
	void validdate(CachedUploadServletConfig config,List<CachedItem> files) throws JfwBaseException;
}
