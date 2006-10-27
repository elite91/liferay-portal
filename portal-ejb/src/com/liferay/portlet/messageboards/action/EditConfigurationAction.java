/**
 * Copyright (c) 2000-2006 Liferay, LLC. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.liferay.portlet.messageboards.action;

import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.Constants;
import com.liferay.portlet.PortletPreferencesFactory;
import com.liferay.util.GetterUtil;
import com.liferay.util.ParamUtil;
import com.liferay.util.StringPool;
import com.liferay.util.StringUtil;
import com.liferay.util.Validator;
import com.liferay.util.servlet.SessionErrors;
import com.liferay.util.servlet.SessionMessages;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * <a href="EditConfigurationAction.java.html"><b><i>View Source</i></b></a>
 *
 * @author  Brian Wing Shun Chan
 *
 */
public class EditConfigurationAction extends PortletAction {

	public void processAction(
			ActionMapping mapping, ActionForm form, PortletConfig config,
			ActionRequest req, ActionResponse res)
		throws Exception {

		String cmd = ParamUtil.getString(req, Constants.CMD);

		if (!cmd.equals(Constants.UPDATE)) {
			return;
		}

		String portletResource = ParamUtil.getString(
			req, "portletResource");

		PortletPreferences prefs = PortletPreferencesFactory.getPortletSetup(
			req, portletResource, false, true);

		String tabs2 = ParamUtil.getString(req, "tabs2");

		if (tabs2.equals("email-from")) {
			updateEmailFrom(req, prefs);
		}
		else if (tabs2.equals("message-added-email")) {
			updateEmailMessageAdded(req, prefs);
		}
		else if (tabs2.equals("message-updated-email")) {
			updateEmailMessageUpdated(req, prefs);
		}
		else if (tabs2.equals("thread-priorities")) {
			updateThreadPriorities(req, prefs);
		}
		else if (tabs2.equals("user-ranks")) {
			updateUserRanks(req, prefs);
		}

		if (SessionErrors.isEmpty(req)) {
			prefs.store();

			SessionMessages.add(req, config.getPortletName() + ".doConfigure");
		}
	}

	public ActionForward render(
			ActionMapping mapping, ActionForm form, PortletConfig config,
			RenderRequest req, RenderResponse res)
		throws Exception {

		return mapping.findForward("portlet.message_boards.edit_configuration");
	}

	protected void updateEmailFrom(ActionRequest req, PortletPreferences prefs)
		throws Exception {

		String emailFromName = ParamUtil.getString(req, "emailFromName");
		String emailFromAddress = ParamUtil.getString(req, "emailFromAddress");

		if (Validator.isNull(emailFromName)) {
			SessionErrors.add(req, "emailFromName");
		}
		else if (!Validator.isEmailAddress(emailFromAddress)) {
			SessionErrors.add(req, "emailFromAddress");
		}
		else {
			prefs.setValue("email-from-name", emailFromName);
			prefs.setValue("email-from-address", emailFromAddress);
		}
	}

	protected void updateEmailMessageAdded(
			ActionRequest req, PortletPreferences prefs)
		throws Exception {

		String emailMessageAddedEnabled = ParamUtil.getString(
			req, "emailMessageAddedEnabled");
		String emailMessageAddedSubjectPrefix = ParamUtil.getString(
			req, "emailMessageAddedSubjectPrefix");
		String emailMessageAddedBody = ParamUtil.getString(
			req, "emailMessageAddedBody");
		String emailMessageAddedSignature = ParamUtil.getString(
			req, "emailMessageAddedSignature");

		if (Validator.isNull(emailMessageAddedSubjectPrefix)) {
			SessionErrors.add(req, "emailMessageAddedSubjectPrefix");
		}
		else if (Validator.isNull(emailMessageAddedBody)) {
			SessionErrors.add(req, "emailMessageAddedBody");
		}
		else {
			prefs.setValue(
				"email-message-added-enabled", emailMessageAddedEnabled);
			prefs.setValue(
				"email-message-added-subject-prefix",
				emailMessageAddedSubjectPrefix);
			prefs.setValue("email-message-added-body", emailMessageAddedBody);
			prefs.setValue(
				"email-message-added-signature", emailMessageAddedSignature);
		}
	}

	protected void updateEmailMessageUpdated(
			ActionRequest req, PortletPreferences prefs)
		throws Exception {

		String emailMessageUpdatedEnabled = ParamUtil.getString(
			req, "emailMessageUpdatedEnabled");
		String emailMessageUpdatedSubjectPrefix = ParamUtil.getString(
			req, "emailMessageUpdatedSubjectPrefix");
		String emailMessageUpdatedBody = ParamUtil.getString(
			req, "emailMessageUpdatedBody");
		String emailMessageUpdatedSignature = ParamUtil.getString(
			req, "emailMessageUpdatedSignature");

		if (Validator.isNull(emailMessageUpdatedSubjectPrefix)) {
			SessionErrors.add(req, "emailMessageUpdatedSubjectPrefix");
		}
		else if (Validator.isNull(emailMessageUpdatedBody)) {
			SessionErrors.add(req, "emailMessageUpdatedBody");
		}
		else {
			prefs.setValue(
				"email-message-updated-enabled", emailMessageUpdatedEnabled);
			prefs.setValue(
				"email-message-updated-subject-prefix",
				emailMessageUpdatedSubjectPrefix);
			prefs.setValue(
				"email-message-updated-body", emailMessageUpdatedBody);
			prefs.setValue(
				"email-message-updated-signature",
				emailMessageUpdatedSignature);
		}
	}

	protected void updateThreadPriorities(
			ActionRequest req, PortletPreferences prefs)
		throws Exception {

		List priorities = new ArrayList();

		for (int i = 0; i < 10; i++) {
			String name = ParamUtil.getString(req, "priorityName" + i);
			String image = ParamUtil.getString(req, "priorityImage" + i);
			double value = ParamUtil.getDouble(req, "priorityValue" + i);

			priorities.add(
				name + StringPool.COMMA + image + StringPool.COMMA + value);
		}

		prefs.setValues(
			"priorities", (String[])priorities.toArray(new String[0]));
	}

	protected void updateUserRanks(ActionRequest req, PortletPreferences prefs)
		throws Exception {

		// Sort ranks by posts

		String[] ranks = StringUtil.split(
			ParamUtil.getString(req, "ranks"), StringPool.NEW_LINE);

		Map map = new TreeMap();

		for (int i = 0; i < ranks.length; i++) {
			String[] kvp = StringUtil.split(ranks[i], StringPool.EQUAL);

			String kvpName = kvp[0];
			Integer kvpPosts = new Integer(GetterUtil.getInteger(kvp[1]));

			map.put(kvpPosts, kvpName);
		}

		ranks = new String[map.size()];

		int count = 0;

		Iterator itr = map.entrySet().iterator();

		while (itr.hasNext()) {
			Map.Entry entry = (Map.Entry)itr.next();

			Integer kvpPosts = (Integer)entry.getKey();
			String kvpName = (String)entry.getValue();

			ranks[count++] = kvpName + StringPool.EQUAL + kvpPosts.toString();
		}

		// Set ranks

		prefs.setValues("ranks", ranks);
	}

}