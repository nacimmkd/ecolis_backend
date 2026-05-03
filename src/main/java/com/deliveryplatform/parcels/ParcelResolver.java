package com.deliveryplatform.parcels;

import com.deliveryplatform.images.Image;
import com.deliveryplatform.images.ImageService;
import com.deliveryplatform.parcels.dto.ParcelDetailedResponse;
import com.deliveryplatform.parcels.dto.ParcelSummaryResponse;
import com.deliveryplatform.profiles.ProfileResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ParcelResolver {

    private final ParcelMapper parcelMapper;
    private final ProfileResolver profileResolver;
    private final ImageService imageService;

    public ParcelSummaryResponse resolveSummary(Parcel parcel) {
        return parcelMapper.toSummaryResponse(parcel)
                .withOwner(profileResolver.resolveSummary(parcel.getOwner().getProfile()))
                .withThumbnailImageUrl(resolveThumbnailUrl(parcel));
    }

    public ParcelDetailedResponse resolveDetailed(Parcel parcel) {
        return parcelMapper.toDetailedResponse(parcel)
                .withOwner(profileResolver.resolveSummary(parcel.getOwner().getProfile()))
                .withThumbnailImageUrl(resolveThumbnailUrl(parcel))
                .withImageUrls(resolveImageUrls(parcel));
    }

    // ----------------------------------------------------------------

    private String resolveThumbnailUrl(Parcel parcel) {
        var thumbnail = parcel.getThumbnailImage();
        if (thumbnail == null || !thumbnail.isConfirmed()) return null;
        return imageService.getReadUrl(thumbnail.getId());
    }

    private List<String> resolveImageUrls(Parcel parcel) {
        if (parcel.getImages() == null) return List.of();
        return parcel.getImages().stream()
                .filter(Image::isConfirmed)
                .map(image -> imageService.getReadUrl(image.getId()))
                .toList();
    }
}