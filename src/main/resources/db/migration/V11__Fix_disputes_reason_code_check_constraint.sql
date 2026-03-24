-- Fix disputes_reason_code_check constraint to include all current enum values
ALTER TABLE disputes DROP CONSTRAINT IF EXISTS disputes_reason_code_check;

ALTER TABLE disputes
    ADD CONSTRAINT disputes_reason_code_check
    CHECK (reason_code IN (
        'ITEM_NOT_AS_DESCRIBED',
        'MISSING_DOCUMENTS',
        'MECHANICAL_FAILURE',
        'DELIVERY_FAILED',
        'WRONG_ITEM',
        'DAMAGED_DURING_DELIVERY',
        'INCOMPLETE_ACCESSORIES',
        'FRAUDULENT_LISTING',
        'SELLER_NOT_RESPONSIVE',
        'PRICE_MISMATCH',
        'OTHER'
    ));
