package pt.ist.socialsoftware.edition.ldod.controller;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import pt.ist.socialsoftware.edition.ldod.domain.LdoD;
import pt.ist.socialsoftware.edition.ldod.domain.LdoDUser;
import pt.ist.socialsoftware.edition.ldod.forms.ChangePasswordForm;
import pt.ist.socialsoftware.edition.ldod.validator.ChangePasswordValidator;

@Controller
@RequestMapping("/user")
public class UserController {
	private static Logger log = LoggerFactory.getLogger(UserController.class);

	@Inject
	private PasswordEncoder passwordEncoder;

	@RequestMapping(method = RequestMethod.GET, value = "/changePassword")
	public ChangePasswordForm passwordForm() {
		return new ChangePasswordForm();
	}

	@RequestMapping(method = RequestMethod.POST, value = "/changePassword")
	public String changePassword(@Valid ChangePasswordForm form, BindingResult formBinding) {
		log.debug("changePassword username:{}", form.getUsername());

		ChangePasswordValidator validator = new ChangePasswordValidator(this.passwordEncoder);
		validator.validate(form, formBinding);

		if (formBinding.hasErrors()) {
			return null;
		}

		LdoDUser user = LdoD.getInstance().getUser(form.getUsername());

		user.updatePassword(this.passwordEncoder, form.getCurrentPassword(), form.getNewPassword());

		return "redirect:/";
	}

}
