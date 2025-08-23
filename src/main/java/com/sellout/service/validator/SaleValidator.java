package com.sellout.service.validator;

import com.sellout.model.Sale;

public class SaleValidator {

    public void validateForCreation(Sale sale) {
        validateBasicFields(sale);
    }

    public void validateForUpdate(Sale sale) {
        if (sale.getId() == null || sale.getId() <= 0) {
            throw new IllegalArgumentException("Sale ID is required for update and must be greater than zero");
        }
        validateBasicFields(sale);
    }

    private void validateBasicFields(Sale sale) {
        if (sale == null) {
            throw new IllegalArgumentException("Sale cannot be null");
        }

        if (!sale.isValidForPersistence()) {
            throw new IllegalArgumentException("Sale contains invalid data");
        }
    }
}