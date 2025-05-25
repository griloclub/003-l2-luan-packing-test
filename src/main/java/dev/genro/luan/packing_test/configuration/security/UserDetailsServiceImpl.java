package dev.genro.luan.packing_test.configuration.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

  private final PasswordEncoder passwordEncoder;

  public UserDetailsServiceImpl(PasswordEncoder passwordEncoder) {
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    if ("user".equals(username)) {
      return new User("user", passwordEncoder.encode("password"), new ArrayList<>());
    } else {
      throw new UsernameNotFoundException("User not found with username: %s".formatted(username));
    }
  }
}
