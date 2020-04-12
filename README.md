[![patreon](https://c5.patreon.com/external/logo/become_a_patron_button.png)](https://www.patreon.com/bePatron?u=12280211)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)

# Keycloak Utilities

This project demonstrates how a third-party application can communicate and manage Keycloak resources via API.

## Authors

 * **Edward P. Legaspi** - *Java Architect* - [czetsuya](https://github.com/czetsuya)
 
 <pre><code>
 # Add role to service account Client Roles section
 final ClientResource clientResource = realmResource.clients().get("0616ecfb-16a6-43bf-807f-f9cf1f096617");
 RoleRepresentation userClientRole = 
      clientResource.roles().get("USER") <- client role name 'USER'
      .toRepresentation();
      
 final UserResource userResourceApiInvestigation = realmResource.users().get(serviceAccountUser.getId());
 userResourceApiInvestigation.roles()
       .clientLevel("0616ecfb-16a6-43bf-807f-f9cf1f096617") <- client id/client UUID
       .add(Arrays.asList(userClientRole));
 </code></pre>
