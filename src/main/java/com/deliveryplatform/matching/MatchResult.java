package com.deliveryplatform.matching;

import com.deliveryplatform.payments.Price;
import com.deliveryplatform.trips.Trip;


/**
 * Résultat d'un matching trip/parcel viable.
 * L'instance n'existe que si le détour est dans la tolérance du trip —
 * pas de champ "viable" séparé à vérifier manuellement.
 */
public record MatchResult(
        Trip trip,
        Price price,
        double score, // plus bas = meilleur match
        boolean viable
) {}