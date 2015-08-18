package org.craftsmenlabs.gareth.rest;

import org.craftsmenlabs.gareth.rest.listener.GarethServletContextListener;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * Created by hylke on 17/08/15.
 */
public class GarethContext {

    public static void main(String[] args) throws Exception {
        final ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        context.setContextPath("/");

        final Server jettyServer = new Server(8080);
        jettyServer.setHandler(context);

        final ServletHolder jerseyServlet = context.addServlet(
                org.glassfish.jersey.servlet.ServletContainer.class, "/*");
        jerseyServlet.setInitOrder(0);

        context.addEventListener(new GarethServletContextListener());

        // Tells the Jersey Servlet which REST service/class to load.
        jerseyServlet.setInitParameter(
                "javax.ws.rs.Application",
                GarethApplication.class.getCanonicalName());

        jettyServer.start();
        jettyServer.join();
    }
}
