package com.deliveryplatform.parcels;

import com.deliveryplatform.images.Image;
import com.deliveryplatform.images.ImageService;
import com.deliveryplatform.parcels.dto.ParcelDetails;
import com.deliveryplatform.parcels.dto.ParcelSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public abstract class ParcelMapperDecorator implements ParcelMapper {

    @Autowired
    private ParcelMapper delegate;

    @Autowired
    private ImageService imageService;

    @Override
    public ParcelSummary toSummaryDto(Parcel parcel) {

        ParcelSummary dto = delegate.toSummaryDto(parcel);

        return dto.toBuilder()
                .thumbnailImageUrl(resolveImageUrl(parcel.getThumbnailImage()))
                .build();
    }

    @Override
    public ParcelDetails toDetailedDto(Parcel parcel) {

        ParcelDetails dto = delegate.toDetailedDto(parcel);

        return dto.toBuilder()
                .thumbnailImageUrl(resolveImageUrl(parcel.getThumbnailImage()))
                .imageUrls(
                        parcel.getImages()
                                .stream()
                                .map(this::resolveImageUrl)
                                .toList()
                )
                .build();
    }

    private String resolveImageUrl(Image image) {

        if (image == null || image.getId() == null) {
            return null;
        }

        return imageService.getReadUrl(image.getId());
    }
}