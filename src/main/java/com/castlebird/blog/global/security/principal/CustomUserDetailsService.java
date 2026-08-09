package com.castlebird.blog.global.security.principal;

import com.castlebird.blog.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    return userRepository.findByEmail(email)
        .map(CustomUserDetails::new)
        .orElseThrow(() -> new UsernameNotFoundException("해당 사용자를 찾을 수 없습니다."));
  }

  public UserDetails loadUserById(Long userId) {
    return userRepository.findById(userId)
        .map(CustomUserDetails::new)
        .orElseThrow(() -> new UsernameNotFoundException("해당 사용자를 찾을 수 없습니다."));
  }
}
