package site.wenjiehou.web.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import site.wenjiehou.domain.Address;
import site.wenjiehou.domain.Profile;
import site.wenjiehou.repository.AddressRepository;
import site.wenjiehou.repository.ProfileRepository;
import site.wenjiehou.security.SecurityUtils;
import site.wenjiehou.web.rest.errors.BadRequestAlertException;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * REST controller for managing {@link site.wenjiehou.domain.Address}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class AddressResource {

    private final Logger log = LoggerFactory.getLogger(AddressResource.class);

    private static final String ENTITY_NAME = "address";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AddressRepository addressRepository;

    @Autowired
    private  ProfileRepository profileRepository;

    public AddressResource(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    /**
     * {@code POST  /addresses} : Create a new address.
     *
     * @param address the address to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new address, or with status {@code 400 (Bad Request)} if the address has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/addresses")
    public ResponseEntity<Address> createAddress(@Valid @RequestBody Address address) throws URISyntaxException {
        log.debug("REST request to save Address : {}", address);
        if (address.getId() != null) {
            throw new BadRequestAlertException("A new address cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Address result = addressRepository.save(address);
        profileRepository.getByUserLogin(SecurityUtils.getCurrentUserLogin().get()).get(0).setAddress(address);
        return ResponseEntity.created(new URI("/api/addresses/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /addresses} : Updates an existing address.
     *
     * @param address the address to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated address,
     * or with status {@code 400 (Bad Request)} if the address is not valid,
     * or with status {@code 500 (Internal Server Error)} if the address couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/addresses")
    public ResponseEntity<Address> updateAddress(@Valid @RequestBody Address address) throws URISyntaxException {
        log.debug("REST request to update Address : {}", address);
        if (address.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Address result = addressRepository.save(address);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, address.getId().toString()))
            .body(result);
    }

    private List<Address> ProfiletoAddress(List<Profile> plist){
        List<Address> alist = new ArrayList<>();
        plist.forEach(item -> {alist.add(item.getAddress());});
        return alist;
    }

    /**
     * {@code GET  /addresses} : get all the addresses.
     *

     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of addresses in body.
     */


    @GetMapping("/addresses")
    public List<Address> getAllAddresses(@RequestParam(required = false) String filter) {
        if ("profile-is-null".equals(filter)) {
            log.debug("REST request to get all Addresss where profile is null");
            return StreamSupport
                .stream(addressRepository.findAll().spliterator(), false)
                .filter(address -> address.getProfile() == null)
                .collect(Collectors.toList());
        }
        log.debug("REST request to get all Addresses");
        if (SecurityUtils.isCurrentUserInRole("ROLE_ADMIN"))
           return addressRepository.findAll();
        else
            return  ProfiletoAddress(profileRepository.getByUserLogin(SecurityUtils.getCurrentUserLogin().get()));
    }

    /**
     * {@code GET  /addresses/:id} : get the "id" address.
     *
     * @param id the id of the address to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the address, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/addresses/{id}")
    public ResponseEntity<Address> getAddress(@PathVariable Long id) {
        log.debug("REST request to get Address : {}", id);
        Optional<Address> address = addressRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(address);
    }

    /**
     * {@code DELETE  /addresses/:id} : delete the "id" address.
     *
     * @param id the id of the address to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/addresses/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long id) {
        log.debug("REST request to delete Address : {}", id);
        addressRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }
}
