package net.tcpshield.tcpshieldapi.impl;

import net.tcpshield.tcpshieldapi.BackendSet;
import net.tcpshield.tcpshieldapi.response.BackendSetResponse;

import java.util.*;

class BackendSetImpl implements BackendSet {

    private final int id;
    private final String name;
    private final List<String> backends;

    BackendSetImpl(int id, String name, List<String> backends) {
        this.id = id;
        this.name = name;
        this.backends = backends;
    }

    BackendSetImpl(BackendSetResponse response) {
        this(response.getID(), response);
    }

    BackendSetImpl(int id, BackendSetResponse response) {
        this.id = id;
        this.name = response.getName();
        this.backends = new ArrayList<>(response.getBackends() == null ? Collections.emptyList() : Arrays.asList(response.getBackends()));
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<String> getBackends() {
        return backends;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BackendSetImpl that = (BackendSetImpl) o;
        return id == that.id && Objects.equals(name, that.name) && Objects.equals(backends, that.backends);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, backends);
    }

    @Override
    public String toString() {
        return "BackendSetImpl{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", backends=" + backends +
                '}';
    }
}
