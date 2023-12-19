package com.sharyar.Electrify.ElectronicsShop.Security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private Logger logger = LoggerFactory.getLogger(OncePerRequestFilter.class);
    @Autowired
    JwtHelper jwtHelper;
    @Autowired
    UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request
            , HttpServletResponse response
            , FilterChain filterChain ) throws ServletException
            , IOException
    {
            //check header name "Authorization" from request
            String requestHeader = request.getHeader("Authorization");
            //this request header isnt actual token. it has token:
            // "Bearer " + token i.e "5454dfsdjf232482.38423dfds23654gdr.fdrr..."
            logger.info("Authroization Header from request = {}" ,requestHeader);
            String username = null;
            String token = null;
            if(requestHeader != null && requestHeader.startsWith("Bearer"))
            {
                //looking good
                token = requestHeader.substring(7);
                try{
                  username = this.jwtHelper.getUsernameFromToken(token);
                }
                catch (IllegalArgumentException e)
                {
                   logger.info("Illegal argument while fetching the username");
                   e.printStackTrace();
                }
                catch (MalformedJwtException e)
                {
                    logger.info("Jwt token is malformed.Some changes have been done in token. Invalid token!!");
                    e.printStackTrace();
                }
                catch (ExpiredJwtException e)
                {
                    logger.info("Jwt token is expired");
                    e.printStackTrace();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                // make sure the user is already not logged in
                if(username != null && SecurityContextHolder.getContext().getAuthentication() == null)
                {
                    logger.info("username from token = {}" , username );
                    logger.info("security context is null. means the user is  not logged in.");
                    // fetch UserDetails i.e User from this username
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    // Now validate token:
                    Boolean tokenValidation = jwtHelper.validateToken(token,userDetails);
                    if(tokenValidation )
                    {
                       // set the authentication object in SecurityContext
                        logger.info("token has been validated");
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        logger.info("Authentication object{user credentials} has been set in" +
                                "Security context");
                    }
                    else
                    {
                        logger.info("Token could not be validated!");
                    }

                }

            }
            else
            {
                logger.info("Authorization header is null or Invalid Authorization header");
            }

            filterChain.doFilter(request,response);

    }

}
