package org.zero.aienglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zero.aienglish.entity.Checkout;

import java.util.UUID;

public interface CheckoutRepository extends JpaRepository<Checkout, UUID> {
}
