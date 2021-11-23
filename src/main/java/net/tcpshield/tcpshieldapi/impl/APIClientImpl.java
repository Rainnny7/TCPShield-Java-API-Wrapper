package net.tcpshield.tcpshieldapi.impl;

import net.tcpshield.tcpshieldapi.APIClient;
import net.tcpshield.tcpshieldapi.BackendSet;
import net.tcpshield.tcpshieldapi.Domain;
import net.tcpshield.tcpshieldapi.Network;
import net.tcpshield.tcpshieldapi.exception.status.NoPermissionException;
import net.tcpshield.tcpshieldapi.request.*;
import net.tcpshield.tcpshieldapi.response.*;
import net.tcpshield.tcpshieldapi.rest.APIConstants;
import net.tcpshield.tcpshieldapi.rest.RequestType;
import net.tcpshield.tcpshieldapi.rest.RestClient;
import net.tcpshield.tcpshieldapi.rest.RestRequest;

import java.util.ArrayList;
import java.util.List;

public class APIClientImpl implements APIClient {

    private final RestClient client;

    public APIClientImpl(String apiKey) {
        if (apiKey == null) throw new IllegalArgumentException("apiKey is null");

        this.client = new RestClient(apiKey);
    }

    @Override
    public List<Network> getNetworks() {
        NetworksResponse response = RestRequest.builder(NetworksResponse.class)
                .url(APIConstants.NETWORKS_ENDPOINT)
                .requestType(RequestType.GET)
                .build()
                .execute(client);

        List<Network> networks = new ArrayList<>();
        for (NetworkResponse networkResponse : response) {
            Network network = new NetworkImpl(networkResponse);
            networks.add(network);
        }

        return networks;
    }

    @Override
    public NetworkPostRepsonse addNetwork(NetworkPostRequest request) {
        return RestRequest.builder(NetworkPostRepsonse.class)
                .url(APIConstants.NETWORKS_ENDPOINT)
                .requestType(RequestType.POST)
                .data(request)
                .build()
                .execute(client);
    }

    @Override
    public Network getNetwork(int id) {
        NetworkResponse response = RestRequest.builder(NetworkResponse.class)
                .url(APIConstants.NETWORK_SPECIFIC_ENDPOINT)
                .pathVariable("networkId", String.valueOf(id))
                .requestType(RequestType.GET)
                .build()
                .execute(client);

        return new NetworkImpl(response);
    }

    @Override
    public void patchNetwork(int id, NetworkPatchRequest request) {
        RestRequest.builder()
                .url(APIConstants.NETWORK_SPECIFIC_ENDPOINT)
                .pathVariable("networkId", String.valueOf(id))
                .requestType(RequestType.PATCH)
                .data(request)
                .build()
                .execute(client);
    }

    @Override
    public void deleteNetwork(int id) {
        RestRequest.builder()
                .url(APIConstants.NETWORK_SPECIFIC_ENDPOINT)
                .pathVariable("networkId", String.valueOf(id))
                .requestType(RequestType.DELETE)
                .build()
                .execute(client);
    }

    @Override
    public List<Domain> getDomains(int networkID) {
        DomainsResponse response = RestRequest.builder(DomainsResponse.class)
                .url(APIConstants.DOMAINS_ENDPOINT)
                .pathVariable("networkId", String.valueOf(networkID))
                .requestType(RequestType.GET)
                .build()
                .execute(client);

        List<Domain> domains = new ArrayList<>();
        for (DomainResponse domainResponse : response) {
            Domain domain = new DomainImpl(domainResponse);
            domains.add(domain);
        }

        return domains;
    }

    @Override
    public DomainPostResponse addDomain(int networkID, DomainPostRequest request) {
        return RestRequest.builder(DomainPostResponse.class)
                .url(APIConstants.DOMAINS_ENDPOINT)
                .pathVariable("networkId", String.valueOf(networkID))
                .requestType(RequestType.POST)
                .data(request)
                .build()
                .execute(client);
    }

    @Override
    public Domain getDomain(int networkID, int domainID) {
        DomainResponse response = RestRequest.builder(DomainResponse.class)
                .url(APIConstants.DOMAIN_SPECIFIC_ENDPOINT)
                .pathVariable("networkId", String.valueOf(networkID))
                .pathVariable("domainId", String.valueOf(domainID))
                .requestType(RequestType.GET)
                .build()
                .execute(client);

        return new DomainImpl(response);
    }

    @Override
    public void patchDomain(int networkID, int domainID, DomainPatchRequest request) {
        RestRequest.builder()
                .url(APIConstants.DOMAIN_SPECIFIC_ENDPOINT)
                .pathVariable("networkId", String.valueOf(networkID))
                .pathVariable("domainId", String.valueOf(domainID))
                .requestType(RequestType.PATCH)
                .data(request)
                .build()
                .execute(client);
    }

    @Override
    public void deleteDomain(int networkID, int domainID) {
        RestRequest.builder()
                .url(APIConstants.DOMAIN_SPECIFIC_ENDPOINT)
                .pathVariable("networkId", String.valueOf(networkID))
                .pathVariable("domainId", String.valueOf(domainID))
                .requestType(RequestType.DELETE)
                .build()
                .execute(client);
    }

    @Override
    public boolean preverify(int networkID, DomainPreverifyRequest request) {
        try {
            return RestRequest.builder(DomainPreverifyResponse.class)
                    .url(APIConstants.DOMAINS_PREVERIFY_ENDPOINT)
                    .pathVariable("networkId", String.valueOf(networkID))
                    .requestType(RequestType.POST)
                    .data(request)
                    .build()
                    .execute(client)
                    .getStatus() == 0;
        } catch (NoPermissionException e) {
            return false;
        }
    }

    @Override
    public boolean verify(int networkID, int domainID) {
        try {
            return RestRequest.builder(DomainVerifyResponse.class)
                    .url(APIConstants.DOMAIN_VERIFY_ENDPOINT)
                    .pathVariable("networkId", String.valueOf(networkID))
                    .pathVariable("domainId", String.valueOf(domainID))
                    .requestType(RequestType.GET)
                    .build()
                    .execute(client)
                    .getStatus() == 0;
        } catch (NoPermissionException e) {
            return false;
        }
    }

    @Override
    public List<BackendSet> getBackendSets(int networkID) {
        BackendSetsResponse response = RestRequest.builder(BackendSetsResponse.class)
                .url(APIConstants.BACKENDS_ENDPOINT)
                .pathVariable("networkId", String.valueOf(networkID))
                .requestType(RequestType.GET)
                .build()
                .execute(client);

        List<BackendSet> backendSets = new ArrayList<>();
        for (BackendSetResponse backendSetResponse : response) {
            BackendSet backendSet = new BackendSetImpl(backendSetResponse);
            backendSets.add(backendSet);
        }

        return backendSets;
    }

    @Override
    public BackendSetPostResponse addBackendSet(int networkID, BackendSetPostRequest request) {
        return RestRequest.builder(BackendSetPostResponse.class)
                .url(APIConstants.BACKENDS_ENDPOINT)
                .pathVariable("networkId", String.valueOf(networkID))
                .requestType(RequestType.POST)
                .data(request)
                .build()
                .execute(client);
    }

    @Override
    public BackendSet getBackendSet(int networkID, int setID) {
        BackendSetResponse response = RestRequest.builder(BackendSetResponse.class)
                .url(APIConstants.BACKENDS_SPECIFIC_ENDPOINT)
                .pathVariable("networkId", String.valueOf(networkID))
                .pathVariable("setId", String.valueOf(setID))
                .requestType(RequestType.GET)
                .build()
                .execute(client);

        return response.getID() == 0 ? new BackendSetImpl(setID, response) : new BackendSetImpl(response);
    }

    @Override
    public void patchBackendSet(int networkID, int setID, BackendSetPatchRequest request) {
        RestRequest.builder(BackendSetResponse.class)
                .url(APIConstants.BACKENDS_SPECIFIC_ENDPOINT)
                .pathVariable("networkId", String.valueOf(networkID))
                .pathVariable("setId", String.valueOf(setID))
                .requestType(RequestType.PATCH)
                .data(request)
                .build()
                .execute(client);
    }

    @Override
    public void deleteBackendSet(int networkID, int id) {
        RestRequest.builder(BackendSetResponse.class)
                .url(APIConstants.BACKENDS_SPECIFIC_ENDPOINT)
                .pathVariable("networkId", String.valueOf(networkID))
                .pathVariable("setId", String.valueOf(id))
                .requestType(RequestType.DELETE)
                .build()
                .execute(client);
    }
}
