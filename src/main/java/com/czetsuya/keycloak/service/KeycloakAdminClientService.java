package com.czetsuya.keycloak.service;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.adapters.RefreshableKeycloakSecurityContext;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.MappingsRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.czetsuya.keycloak.KeycloakAdminClientConfig;
import com.czetsuya.keycloak.KeycloakAdminClientUtils;
import com.czetsuya.keycloak.KeycloakPropertyReader;
import com.czetsuya.security.CurrentUserProvider;

import javax.ws.rs.core.Response;

/**
 * @author Edward P. Legaspi | czetsuya@gmail.com
 * 
 * @version 0.0.1
 * @since 0.0.1
 */
@Service
public class KeycloakAdminClientService {

    @Value("${keycloak.resource}")
    private String keycloakClient;

    @Autowired
    private CurrentUserProvider currentUserProvider;

    @Autowired
    private KeycloakPropertyReader keycloakPropertyReader;

    public List<String> getCurrentUserRoles() {
        return currentUserProvider.getCurrentUser().getRoles();
    }

    public Object getUserProfileOfLoggedUser() {

        @SuppressWarnings("unchecked")
        KeycloakPrincipal<RefreshableKeycloakSecurityContext> principal = (KeycloakPrincipal<RefreshableKeycloakSecurityContext>) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        KeycloakAdminClientConfig keycloakAdminClientConfig = KeycloakAdminClientUtils.loadConfig(keycloakPropertyReader);
        Keycloak keycloak = KeycloakAdminClientUtils.getKeycloakClient(principal.getKeycloakSecurityContext(), keycloakAdminClientConfig);

        // Get realm
        RealmResource realmResource = keycloak.realm(keycloakAdminClientConfig.getRealm());

//        realmResource.clients().findAll().forEach(client -> {
//            System.out.println(client.getId() + "name:" + client.getName()
//                    +  " clientId:" + client.getClientId()
//            );
//        });

        final ClientResource clientResource = realmResource.clients().get("0616ecfb-16a6-43bf-807f-f9cf1f096617");//resource server client uid
        final UserRepresentation serviceAccountUser = clientResource.getServiceAccountUser();
        final ClientRepresentation clientRepresentation = clientResource.toRepresentation();
        
        final UserResource userResourceApiInvestigation = realmResource.users().get(serviceAccountUser.getId());
        System.out.println("serviceAccountUser.getId():" + serviceAccountUser.getId());
        final MappingsRepresentation roleMapping = userResourceApiInvestigation.roles().getAll();
        roleMapping.getClientMappings().forEach((key, value) -> {
            System.out.println("client role:" + key + " -> " + value);
            System.out.println(value.getMappings());
        });

        clientResource.roles().list().forEach(role -> {
            System.out.println("client role id:" + role.getId()
                    + "\ngetContainerId:" + role.getContainerId()
                    + "\nrole name:" + role.getName()
            );
        });

        ClientRepresentation clientRepresentationCreate = new ClientRepresentation();
        clientRepresentationCreate.setClientId("assad-app-api-02");
        clientRepresentationCreate.setSecret("assad-app-api-02");
        clientRepresentationCreate.setProtocol("openid-connect");
        clientRepresentationCreate.setServiceAccountsEnabled(true);
        clientRepresentationCreate.setPublicClient(false);
        clientRepresentationCreate.setBearerOnly(false);
        clientRepresentationCreate.setRedirectUris(Arrays.asList("http://localhost:8080"));
        final Response response = realmResource.clients().create(clientRepresentationCreate);
        System.out.println(response.getLocation());

        response.getHeaders().forEach((key,value) -> {
            System.out.println(key + " response value:" + value);
        });

//        System.out.println("response entity" + response.getEntity());
        System.out.println("response read:--" + response.readEntity(String.class) + "<<");

//        RoleRepresentation userClientRole =
//                clientResource.roles().get("USER").toRepresentation();
//
//        userResourceApiInvestigation.roles().clientLevel("0616ecfb-16a6-43bf-807f-f9cf1f096617")
//                .add(Arrays.asList(userClientRole));



        /*
[
  {
    "id": "43ddeaf7-5f9f-46e3-a8ee-efd122f5ddd1", <- role id
    "name": "USER",
    "composite": false,
    "clientRole": true,
    "containerId": "0616ecfb-16a6-43bf-807f-f9cf1f096617"
  }
]
         */


//        realmResource.roles()
//        Request Method: POST
//        http://localhost:8080/auth/admin/
//        realms/balambgarden
//        /users/18f436d5-ff38-44f6-b8aa-34707de0c57a <- service user account id
//        /role-mappings/clients/0616ecfb-16a6-43bf-807f-f9cf1f096617 <- client id


        UsersResource usersResource = realmResource.users();
        UserResource userResource = usersResource.get(currentUserProvider.getCurrentUser().getUserId());
        UserRepresentation userRepresentation = userResource.toRepresentation();

        return userRepresentation;
    }

    public void createClient(RealmResource realmResource) {
        final ClientsResource clientsResource = realmResource.clients();
        ClientRepresentation clientRepresentation = new ClientRepresentation();
        clientRepresentation.setName("");
        clientsResource.create(clientRepresentation);
        final ClientResource clientResource = clientsResource.get("");
//        final ClientRepresentation clientRepresentation = clientResource.toRepresentation();

    }
}
