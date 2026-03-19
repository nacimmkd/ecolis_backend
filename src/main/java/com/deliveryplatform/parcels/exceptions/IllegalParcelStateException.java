package com.deliveryplatform.parcels.exceptions;

import com.deliveryplatform.parcels.ParcelStatus;

public class IllegalParcelStateException extends RuntimeException {
    public IllegalParcelStateException() {
        super("Cannot update a parcel with status : " + ParcelStatus.AVAILABLE);
    }
}
