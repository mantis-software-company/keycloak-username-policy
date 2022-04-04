package tr.com.mantis.keycloak.registration;

import org.keycloak.authentication.forms.RegistrationPage;
import org.keycloak.events.Details;
import org.keycloak.events.Errors;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.authentication.FormContext;
import java.util.regex.Matcher;
import org.keycloak.models.AuthenticatorConfigModel;
import java.util.regex.Pattern;
import javax.ws.rs.core.MultivaluedMap;
import org.keycloak.models.utils.FormMessage;
import java.util.ArrayList;
import org.keycloak.authentication.ValidationContext;
import org.keycloak.provider.ProviderConfigProperty;
import java.util.List;
import org.keycloak.authentication.FormAction;
import org.keycloak.authentication.forms.RegistrationProfile;
import org.keycloak.services.validation.Validation;
import org.keycloak.services.messages.Messages;


public class RegistrationProfileWithUsernamePolicy extends RegistrationProfile implements FormAction
{
    private static final List<ProviderConfigProperty> CONFIG_PROPERTIES = new ArrayList<>();

    @Override
    public String getDisplayType() {
        return "Username policy check";
    }

    @Override
    public String getId() {
        return "username-policy-check-action";
    }

    @Override
    public boolean isConfigurable() {
        return true;
    }

    @Override
    public String getHelpText() {
        return "Allows defining username policy rules with Regex";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return CONFIG_PROPERTIES;
    }

    @Override
    public void validate(final ValidationContext context) {
        final MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        final List<FormMessage> errors = new ArrayList<>();
        final String username = formData.getFirst(Validation.FIELD_USERNAME);
        final AuthenticatorConfigModel usernamePolicyConfig = context.getAuthenticatorConfig();
        final String eventError = Errors.INVALID_REGISTRATION;
        if (username == null) {
            context.getEvent().detail(Details.USERNAME, username);
            errors.add(new FormMessage(RegistrationPage.FIELD_USERNAME, Messages.MISSING_USERNAME));
            context.error(eventError);
            context.validationError(formData, errors);
            return;
        }

        String policyRegex = "^[a-zA-Z0-9\\.-_]+$";
        if(usernamePolicyConfig != null){
            policyRegex = usernamePolicyConfig
                    .getConfig()
                    .getOrDefault("usernameValidationRegex", "^[a-zA-Z0-9\\.-_]+$");
        }

        final Pattern pattern = Pattern.compile(policyRegex);
        final Matcher matcher = pattern.matcher(username);
        if (!matcher.find()) {
            context.getEvent().detail(Details.USERNAME, username);
            errors.add(new FormMessage(RegistrationPage.FIELD_USERNAME, Messages.INVALID_USERNAME));
        }
        if (errors.size() > 0) {
            context.error(eventError);
            context.validationError(formData, errors);
            return;
        }
        context.success();
    }

    public void buildPage(final FormContext context, final LoginFormsProvider form) {
        AuthenticatorConfigModel conf = context.getAuthenticatorConfig();
        if(conf != null){
            final String usernamePolicyFormText = conf
                    .getConfig()
                    .getOrDefault("usernamePolicyFormText", "");
            form.setAttribute("usernamePolicyFormText", usernamePolicyFormText);
        }else {
            form.setAttribute("usernamePolicyFormText", "");
        }
    }

    static {

        final ProviderConfigProperty regexField = new ProviderConfigProperty();
        regexField.setName("usernameValidationRegex");
        regexField.setLabel("Regex for username validation");
        regexField.setType(ProviderConfigProperty.STRING_TYPE);
        regexField.setHelpText("Username policy rules Regex");

        final ProviderConfigProperty policyFormTextField = new ProviderConfigProperty();
        policyFormTextField.setName("usernamePolicyFormText");
        policyFormTextField.setLabel("Policy form text");
        policyFormTextField.setType(ProviderConfigProperty.TEXT_TYPE);
        policyFormTextField.setHelpText("Policy form text can be used to give users more descriptive information about the username policy you have created. The text you type here will appear in the registration form.");

        RegistrationProfileWithUsernamePolicy.CONFIG_PROPERTIES.add(regexField);
        RegistrationProfileWithUsernamePolicy.CONFIG_PROPERTIES.add(policyFormTextField);
    }
}