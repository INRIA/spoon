package com.inspirenetz.api.core.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.cache.NullUserCache;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by sandheepgr on 6/9/14.
 */
public class IPBasedAuthenticationFilter extends BasicAuthenticationFilter {

    // Create the logger
    private static Logger log = LoggerFactory.getLogger(IPBasedAuthenticationFilter.class);


    private AuthenticationDetailsSource<HttpServletRequest,?> authenticationDetailsSource = new WebAuthenticationDetailsSource();

    protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

    private IPBasedAuthenticationEntryPoint authenticationEntryPoint;

    private UserDetailsService userDetailsService;

    private UserCache userCache = new NullUserCache();

    private Environment environment;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        // Parse to request
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        // Parse to response
        HttpServletResponse response = (HttpServletResponse) servletResponse;


        // Get the username from the request
        String username = request.getHeader("in-username");

        // Get the authHeader
        String authHeader = request.getHeader("Authorization");

        // If the authHeader is starting with Digest, then return
        if ( authHeader != null && authHeader.startsWith("Digest") ) {

            // Do the filter chain
            filterChain.doFilter(request,response);

            // Return
            return ;

        }

        // If the username is not present, then we need to have the chain filter and return
        if ( username == null ) {

            username = "localipuser";

        }

        // Check if the user is behind a proxy ( then get the original ip )
        String incomingIP = request.getHeader("X-FORWARDED-FOR");

        // If the incomingIP is not forwarded, then use g-ip
        if ( incomingIP == null ) {

            // Get the incomingIP
            incomingIP = request.getHeader("g-ip");


        }

        // Check if its null and get the remote ip address
        if ( incomingIP == null ) {

            // Get the incomingIP
            incomingIP = request.getRemoteAddr();


        }


        // Log the ip information
        log.info("Incoming IP : " + incomingIP);

        // Check for the validity of the ipAddress
        if ( !isRemoteIPAllowed(incomingIP) ) {

            /*
            // Set the SecurityContextHolder authentication to null
            SecurityContextHolder.getContext().setAuthentication(null);

            // Call the commence method of authenticationEntryPoint
            authenticationEntryPoint.commence(request,response,new BadCredentialsException("IP not allowed"));
            */

            // Do filter to digest
            filterChain.doFilter(servletRequest,servletResponse);

            // return the control
            return ;

        }


        // Set the cacheUser to fals
        boolean cacheUsed = false;

        // Try to get the user from the cache
        UserDetails user = userCache.getUserFromCache(username);


        // If the user was not found on the cache, check in the database
        if ( user == null ) {

            // Load the user from the dao
            user  = userDetailsService.loadUserByUsername(username);


            // If again the user is null, then throw exception
            if (user == null) {

                // Throw exception
                throw new AuthenticationServiceException(
                        "AuthenticationDao returned null, which is an interface contract violation");

            }


            // Otherwise put the user in cache
            userCache.putUserInCache(user);

        }


        // Create the session for the user
        UsernamePasswordAuthenticationToken authRequest;

        // Create the authRequest
        authRequest = new UsernamePasswordAuthenticationToken(user, user.getPassword());

        // Set the details
        authRequest.setDetails(authenticationDetailsSource.buildDetails((HttpServletRequest) request));

        // Set the authentication in the security context holder
        SecurityContextHolder.getContext().setAuthentication(authRequest);

        // Call the chain.doFilter
        filterChain.doFilter(request,response);


    }



    public boolean isRemoteIPAllowed(String ipAddress) {

        // Get the property for the passed ip
        String value = environment.getProperty(ipAddress);

        // If the value is null or "deny", then return false, else return true
        if ( value == null || value.equals("deny") ) {

            return false;

        }


        // otherwise return true
        return true;

    }


    public IPBasedAuthenticationEntryPoint getAuthenticationEntryPoint() {
        return authenticationEntryPoint;
    }

    public void setAuthenticationEntryPoint(IPBasedAuthenticationEntryPoint authenticationEntryPoint) {
        this.authenticationEntryPoint = authenticationEntryPoint;
    }


    public UserDetailsService getUserDetailsService() {
        return userDetailsService;
    }

    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }


    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
