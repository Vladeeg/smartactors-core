package info.smart_tools.smartactors.core.plugin_loader_from_jar;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

/**
 * Extension of {@link URLClassLoader}
 */
public class ExpansibleURLClassLoader extends URLClassLoader {

    /**
     * Redefined constructor
     * @param urls the URLs from which to load classes and resources
     */
    public ExpansibleURLClassLoader(final URL[] urls) {
        super(urls);
    }

    /**
     * Add new instance of {@link URL} to the current url class loader
     * @param url instance of {@link URL}
     */
    public void addUrl(final URL url) {
        URL[] urls = getURLs();
        if (Arrays.asList(urls).contains(url)) {
            return;
        }
        addURL(url);
    }
}
