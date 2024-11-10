package com.vertmix.supervisor.repository;

public interface PlayerRepository<P, T> extends Repository<T>{

    void login(P player);

    void logout(P player);
}
