package org.jfw.apt.web.handlers;

import java.util.ListIterator;

import org.jfw.apt.exception.AptException;
import org.jfw.apt.model.MethodParamEntry;
import org.jfw.apt.out.ClassWriter;
import org.jfw.apt.web.RequestHandler;
import org.jfw.apt.web.annotation.LoginUser;

public class LoginUserHandler extends RequestHandler {

	@Override
	public void init() {
	}

	@Override
	public void appendBeforCode(ClassWriter cw) throws AptException {
		LoginUser user = this.aptWebHandler.getMe().getRef().getAnnotation(LoginUser.class);
		String userName = null;
		String pt = "org.jfw.util.auth.AuthUser";
		if (user == null) {
			for (ListIterator<MethodParamEntry> it = this.aptWebHandler.getMe().getParams().listIterator(); it
					.hasNext();) {
				MethodParamEntry mpe = it.next();
				user = mpe.getRef().getAnnotation(LoginUser.class);
				if (user != null) {
					userName = mpe.getName();
					pt = mpe.getTypeName();
					break;
				}
			}
		}
		if (user == null)
			return;
		if (userName == null)
			userName = cw.getMethodTempVarName();
		this.aptWebHandler.readSession();
		cw.bL(pt).w(" ").w(userName).w(" = (").w(pt)
				.el(")session.getAttribute(org.jfw.util.auth.AuthUser.LOGIN_USER_FLAG_IN_SESSION);");
		if (user.value() || (user.auth() > 0)) {
			cw.bL("if(null == ").w(userName)
					.el(") throw new org.jfw.util.exception.JfwBaseException(1,\"no user login\");");
		}
		if (user.auth() > 0) {
			cw.bL("if(!").w(userName).w(".hasAuthority(").w(user.auth())
					.el(")throw new org.jfw.util.exception.JfwBaseException(100,\"Insufficient authority\");");
		}

	}



}
