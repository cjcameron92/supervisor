package com.vertmix.supervisor.menu;

import com.vertmix.supervisor.core.CoreProvider;
import com.vertmix.supervisor.core.annotation.Navigation;
import com.vertmix.supervisor.core.module.Module;
import com.vertmix.supervisor.core.service.Services;
import com.vertmix.supervisor.reflection.AbstractProxyHandler;

import java.io.File;
import java.lang.reflect.Proxy;

public class MenuModule implements Module<Object> {

    private File folder = null;

    @Override
    public void onEnable(CoreProvider<Object> provider) {
        folder = provider.getPath().toFile();
        Services.register(Menu.class, clazz -> {
            File file = folder;
            Navigation navigation = clazz.getAnnotation(Navigation.class);
            if (navigation != null) {
                file = new File(navigation.path());
            }

            return newRepository(clazz, new MenuProxyHandler(clazz, file));
        });

        Services.register(PagedMenu.class, clazz -> {
            File file = folder;
            Navigation navigation = clazz.getAnnotation(Navigation.class);
            if (navigation != null) {
                file = new File(navigation.path());
            }

            return newPagedRepository(clazz, new PagedMenuProxyHandler(clazz, file));
        });

        // TODO: 2024-11-09 Implement listener logic for menus
    }

    @Override
    public void onDisable() {

    }

    public static Menu newRepository(Class<Menu> clazz, AbstractProxyHandler<Menu> handler) {
        return (Menu) Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class<?>[]{clazz},
                handler
        );
    }

    public static PagedMenu newPagedRepository(Class<PagedMenu> clazz, AbstractProxyHandler<PagedMenu> handler) {
        return (PagedMenu) Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class<?>[]{clazz},
                handler
        );
    }
}
