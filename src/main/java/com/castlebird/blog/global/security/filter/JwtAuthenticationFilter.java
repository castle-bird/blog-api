package com.castlebird.blog.global.security.filter;

import com.castlebird.blog.global.security.handler.JwtUnauthorizedHandler;
import com.castlebird.blog.global.security.principal.CustomUserDetailsService;
import com.castlebird.blog.global.security.token.AccessTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private static final String BEARER_PREFIX = "Bearer ";

  private final AccessTokenProvider accessTokenProvider;
  private final CustomUserDetailsService customUserDetailsService;
  private final JwtUnauthorizedHandler jwtUnauthorizedHandler;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain
  ) throws ServletException, IOException {

    String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

    if (authorization == null) {
      filterChain.doFilter(request, response);
      return;
    }

    if (!authorization.startsWith(BEARER_PREFIX)) {
      sendUnauthorized(request, response);
      return;
    }

    try {
      String accessToken = authorization.substring(BEARER_PREFIX.length());
      Long userId = accessTokenProvider.getUserId(accessToken);
      UserDetails userDetails = customUserDetailsService.loadUserById(userId);

      UsernamePasswordAuthenticationToken authentication =
          new UsernamePasswordAuthenticationToken(
              userDetails,
              null,
              userDetails.getAuthorities()
          );

      authentication.setDetails(
          new WebAuthenticationDetailsSource().buildDetails(request)
      );

      SecurityContextHolder.getContext().setAuthentication(authentication);

      filterChain.doFilter(request, response);
    } catch (IllegalArgumentException | UsernameNotFoundException e) {
      SecurityContextHolder.clearContext();
      sendUnauthorized(request, response);
    }
  }

  private void sendUnauthorized(
      HttpServletRequest request,
      HttpServletResponse response
  ) throws IOException {
    jwtUnauthorizedHandler.commence(
        request,
        response,
        new BadCredentialsException("유효하지 않은 Access Token입니다.")
    );
  }
}
