# keycloak-username-policy
Keycloak Username Policy SPI for Keycloak

## Usage

1. Run `mvn clean install` or download it for keycloak 16.1.1 from https://s01.oss.sonatype.org/service/local/repositories/releases/content/tr/com/mantis/keycloak/keycloak-username-policy/16.1.1/keycloak-username-policy-16.1.1.jar and copy it to `/opt/jboss/keycloak/standalone/deployments/`.
2. After restart Keycloak, select your realm, click Configure > Authentication on menu and select "Registration" flow on Flows tab then click "Copy" button to copy registration flow. 
3. Select copied registration flow and select "Add execution" from Actions menu on parent registration form. 
4. Select "Username Policy Check" as provider then save. 