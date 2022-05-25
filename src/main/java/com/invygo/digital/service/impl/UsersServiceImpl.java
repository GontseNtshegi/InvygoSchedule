package com.invygo.digital.service.impl;

import com.invygo.digital.domain.Users;
import com.invygo.digital.repository.UsersRepository;
import com.invygo.digital.service.UsersService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Users}.
 */
@Service
@Transactional
public class UsersServiceImpl implements UsersService {

    private final Logger log = LoggerFactory.getLogger(UsersServiceImpl.class);

    private final UsersRepository usersRepository;

    public UsersServiceImpl(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public Mono<Users> save(Users users) {
        log.debug("Request to save Users : {}", users);
        return usersRepository.save(users);
    }

    @Override
    public Mono<Users> update(Users users) {
        log.debug("Request to save Users : {}", users);
        return usersRepository.save(users);
    }

    @Override
    public Mono<Users> partialUpdate(Users users) {
        log.debug("Request to partially update Users : {}", users);

        return usersRepository
            .findById(users.getId())
            .map(existingUsers -> {
                if (users.getEmail() != null) {
                    existingUsers.setEmail(users.getEmail());
                }
                if (users.getName() != null) {
                    existingUsers.setName(users.getName());
                }
                if (users.getSurname() != null) {
                    existingUsers.setSurname(users.getSurname());
                }
                if (users.getPassword() != null) {
                    existingUsers.setPassword(users.getPassword());
                }

                return existingUsers;
            })
            .flatMap(usersRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<Users> findAll() {
        log.debug("Request to get all Users");
        return usersRepository.findAll();
    }

    public Mono<Long> countAll() {
        return usersRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Users> findOne(Long id) {
        log.debug("Request to get Users : {}", id);
        return usersRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Users : {}", id);
        return usersRepository.deleteById(id);
    }
}
