package io.github.anoobizzz;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.util.jar.JarFile;
import java.util.logging.Logger;

public class SimpleJarLoader {
    private static final Logger LOGGER = Logger.getLogger(SimpleJarLoader.class.getName());

    public static void premain(final String agentArgs, final Instrumentation inst) {
        LOGGER.info("Adding jars...");

        if (agentArgs == null) {
            LOGGER.info("No paths were provided, skipping");
            return;
        }

        final String[] paths = agentArgs.split(",");
        for (final String path : paths) {
            addFromPath(path, inst);
        }
    }

    private static void addFromPath(final String path, final Instrumentation inst) {
        final File[] files;
        try {
            files = new File(path).listFiles((dir, name) -> name.endsWith(".jar"));
        } catch (final Exception e) {
            LOGGER.severe("Failed to collect jars: " + e.getMessage());
            return;
        }

        if (files == null) {
            LOGGER.info("Provided path is not a directory or no jars were found in it");
            return;
        }

        for (final File jar : files) {
            try {
                final JarFile jarfile = new JarFile(jar);
                inst.appendToSystemClassLoaderSearch(jarfile);
                LOGGER.info("Added jar: " + jarfile.getName());
            } catch (final Exception e) {
                LOGGER.severe("Failed to add jar to classloader: " + e.getMessage());
            }
        }
    }
}