package co.edu.uco.ucochallenge.user.registeruser.application.usecase.domain.rules;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import co.edu.uco.ucochallenge.domain.common.specification.Specification;

class SpecificationCompositionTest {

        @Test
        void shouldComposeSpecificationsUsingAndOrNot() {
                Specification<Integer> greaterThanTen = value -> value > 10;
                Specification<Integer> even = value -> value % 2 == 0;

                Specification<Integer> composed = greaterThanTen.and(even);

                assertTrue(composed.isSatisfiedBy(12));
                assertFalse(composed.isSatisfiedBy(9));
                assertTrue(greaterThanTen.or(even).isSatisfiedBy(8));
                assertTrue(even.not().isSatisfiedBy(3));
        }
}
