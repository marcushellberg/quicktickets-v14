package com.vaadin.demo.dashboard;

import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.di.DefaultInstantiator;
import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.i18n.I18NProvider;
import com.vaadin.flow.internal.ReflectTools;
import com.vaadin.flow.server.Constants;
import com.vaadin.flow.server.InvalidI18NConfigurationException;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServiceInitListener;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public class CustomInstantiator extends DefaultInstantiator {
 
    private VaadinService service;
    private static final AtomicReference<I18NProvider> i18nProvider = new AtomicReference<>();

    public CustomInstantiator() {
        super(VaadinService.getCurrent());
    }

    @Override
    public boolean init(VaadinService service) {
        this.service = service;
        return true;
    }

    @Override
    public Stream<VaadinServiceInitListener> getServiceInitListeners() {
        return getServiceLoaderListeners(service.getClassLoader());
    }

    @Override
    public <T> T getOrCreate(Class<T> type) {
        T statefulComponent = ComponentUtil.getData(UI.getCurrent(), type);
        if (statefulComponent != null) {
            return statefulComponent;
        }
        T component = super.getOrCreate(type);

        // Store stateful views to the UI
        for(Annotation annotation : component.getClass().getAnnotations()) {
            if(annotation instanceof PreserveOnNavigation){
                ComponentUtil.setData(UI.getCurrent(), type, component);
                break;
            }
        }

        return component;
    }

    @Override
    public I18NProvider getI18NProvider() {
        if (i18nProvider.get() == null) {
            i18nProvider.compareAndSet(null, getI18NProviderInstance());
        }
        return i18nProvider.get();
    }

    private I18NProvider getI18NProviderInstance() {
        String property = getI18NProviderProperty();
        if (property == null) {
            return null;
        }
        try {
            // Get i18n provider class if found in application
            // properties
            Class<?> providerClass = DefaultInstantiator.class.getClassLoader()
                    .loadClass(property);
            if (I18NProvider.class.isAssignableFrom(providerClass)) {

                return ReflectTools.createInstance(
                        (Class<? extends I18NProvider>) providerClass);
            }
        } catch (ClassNotFoundException e) {
            throw new InvalidI18NConfigurationException(
                    "Failed to load given provider class '" + property
                            + "' as it was not found by the class loader.",
                    e);
        }
        return null;
    }

    /**
     * Get the I18NProvider property from the session configurator or try to
     * load it from application.properties property file.
     *
     * @return I18NProvider parameter or null if not found
     */
    private String getI18NProviderProperty() {
        DeploymentConfiguration deploymentConfiguration = service
                .getDeploymentConfiguration();
        if (deploymentConfiguration == null) {
            return null;
        }
        return deploymentConfiguration
                .getStringProperty(Constants.I18N_PROVIDER, null);
    }

}