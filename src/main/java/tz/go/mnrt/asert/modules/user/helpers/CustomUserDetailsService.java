package tz.go.mnrt.asert.modules.user.helpers;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.stereotype.Service;
import tz.go.mnrt.asert.modules.user.repository.UserRepository;

import java.util.ArrayList;

/**
 * @author developer
 */
@Service
@Qualifier("userDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

  @Autowired private UserRepository repo;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return repo.findUserByEmailIgnoreCase(username)
        .map(
            u -> {
              // Check if user is approved
              if (u.getIsApproved() == null || !u.getIsApproved()) {
                throw new DisabledException("You need to be approved first to login");
              }
              
              return new org.springframework.security.core.userdetails.User(
                  u.getEmail(), u.getPassword(), true, true, true, true, new ArrayList<>());
            })
        .orElseThrow(
            () ->
                new UsernameNotFoundException(
                    "No user with " + "the name " + username + "was found in the database"));
  }
}
