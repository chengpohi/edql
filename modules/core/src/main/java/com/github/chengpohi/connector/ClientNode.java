package com.github.chengpohi.connector;

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.InternalSettingsPreparer;
import org.elasticsearch.node.Node;
import org.elasticsearch.plugins.Plugin;

import java.util.Collection;
import java.util.Collections;

/**
 * Created by xiachen on 29/11/2016.
 */
public class ClientNode extends Node {
    public ClientNode(Settings preparedSettings, Collection<Class<? extends Plugin>> classpathPlugins) {
        super(InternalSettingsPreparer.prepareEnvironment(preparedSettings,
            Collections.emptyMap(),
            null,
            () -> "eqlNode"),
            classpathPlugins,
            false);
    }
}
