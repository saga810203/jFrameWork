package org.jfw.apt.web.handlers;

import java.util.List;

import org.jfw.apt.exception.AptException;
import org.jfw.apt.model.MethodParamEntry;
import org.jfw.apt.out.ClassWriter;
import org.jfw.apt.web.RequestHandler;
import org.jfw.apt.web.handlers.buildParam.BuildParameter;
import org.jfw.apt.web.handlers.buildParam.DefineHandler;
import org.jfw.apt.web.handlers.buildParam.NopHandler;
import org.jfw.apt.web.handlers.buildParam.PVarHandler;
import org.jfw.apt.web.handlers.buildParam.ParameterMapHandler;
import org.jfw.apt.web.handlers.buildParam.PathVarHandler;
import org.jfw.apt.web.handlers.buildParam.RequestBodyHandler;
import org.jfw.apt.web.handlers.buildParam.RequestHeaderHandler;
import org.jfw.apt.web.handlers.buildParam.RequestParamHandler;
import org.jfw.apt.web.handlers.buildParam.SessionValHandler;
import org.jfw.apt.web.handlers.buildParam.UploadHandler;

public class BuildParamHandler extends RequestHandler {

	private static final BuildParameter[] hanlders = new BuildParameter[] { DefineHandler.INS, NopHandler.INS,
			ParameterMapHandler.INS, PathVarHandler.INS,PVarHandler.INS ,RequestBodyHandler.INS,RequestHeaderHandler.INS,
			RequestParamHandler.INS,SessionValHandler.INS,UploadHandler.INS};

	@Override
	public void init() {
	}

	private BuildParameter getBuilderParameter(MethodParamEntry mpe) throws AptException {

		for (BuildParameter bp : hanlders) {
			if (bp.match(mpe.getRef())) {
				return bp;
			}
		}
		return RequestParamHandler.INS;
	}

	@Override
	public void appendBeforCode(ClassWriter cw) throws AptException {
		List<MethodParamEntry> mpes = this.aptWebHandler.getMe().getParams();
		for (MethodParamEntry mpe : mpes) {
			BuildParameter bp = this.getBuilderParameter(mpe);
			bp.build(cw, mpe, this.aptWebHandler);
		}
	}
}
